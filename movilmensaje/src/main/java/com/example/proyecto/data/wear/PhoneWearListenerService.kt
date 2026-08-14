package com.example.proyecto.data.wear

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.proyecto.data.repository.RutinasRepository
import com.example.proyecto.ui.entrenamiento.EntrenamientoActivoActivity
import com.example.proyecto.utils.TokenManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Recibe los mensajes que manda el módulo del reloj (fittrackwear)
 * a través de la Wearable Data Layer API.
 *
 * El reloj manda estos paths (ver WearConstants.kt en fittrackwear):
 *  - PATH_START_WORKOUT (con el nombre de la rutina como contenido)
 *  - PATH_PAUSE_WORKOUT
 *  - PATH_RESUME_WORKOUT
 *  - PATH_FINISH_WORKOUT
 *  - PATH_REQUEST_ROUTINES (pide la lista real de rutinas activas)
 *
 * Se ejecuta en segundo plano, fuera del ciclo de vida de cualquier
 * Activity. PAUSE/RESUME/FINISH se reenvían a EntrenamientoBridge —
 * si EntrenamientoActivoActivity ya está abierta (porque el
 * entrenamiento lo inició el teléfono), actualiza su UI en tiempo
 * real. START abre EntrenamientoActivoActivity si fue el reloj quien
 * inició. REQUEST_ROUTINES responde con las rutinas reales y
 * activas de la API, cada una con sus ejercicios completos, para que
 * el reloj pueda correr una rutina completa por su cuenta sin datos
 * simulados.
 */
class PhoneWearListenerService : WearableListenerService() {

    companion object {
        private const val TAG = "PhoneWearListener"

        // Deben coincidir exactamente con WearConstants.kt del módulo fittrackwear
        private const val PATH_START_WORKOUT = "/fittrack/workout/start"
        private const val PATH_PAUSE_WORKOUT = "/fittrack/workout/pause"
        private const val PATH_RESUME_WORKOUT = "/fittrack/workout/resume"
        private const val PATH_FINISH_WORKOUT = "/fittrack/workout/finish"
        private const val PATH_REQUEST_ROUTINES = "/fittrack/routines/request"
        private const val PATH_ROUTINES_LIST = "/fittrack/routines/list"
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    // Un WearableListenerService no tiene un lifecycleScope propio
    // (no es una Activity ni un ViewModel), así que se le da uno
    // manual para poder consultar la API real y se cancela en
    // onDestroy().
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val rutinasRepository = RutinasRepository()

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        val path = messageEvent.path
        val mensaje = messageEvent.data.toString(Charsets.UTF_8)

        Log.d(TAG, "Mensaje recibido del reloj. Ruta: $path, contenido: $mensaje")

        // onMessageReceived no llega en el hilo principal; tanto el
        // bridge como lanzar una Activity necesitan el hilo
        // principal, así que se despacha ahí.
        mainHandler.post {
            when (path) {

                PATH_START_WORKOUT -> {
                    Log.d(TAG, "El reloj inició el entrenamiento: $mensaje")
                    abrirEntrenamientoDesdeReloj(nombreRutina = mensaje)
                }

                PATH_PAUSE_WORKOUT -> {
                    Log.d(TAG, "El reloj pausó el entrenamiento.")
                    EntrenamientoBridge.notificarPausar()
                }

                PATH_RESUME_WORKOUT -> {
                    Log.d(TAG, "El reloj reanudó el entrenamiento.")
                    EntrenamientoBridge.notificarReanudar()
                }

                PATH_FINISH_WORKOUT -> {
                    Log.d(TAG, "El reloj finalizó el entrenamiento.")
                    EntrenamientoBridge.notificarFinalizar()
                }

                PATH_REQUEST_ROUTINES -> {
                    Log.d(TAG, "El reloj pidió la lista de rutinas reales.")
                    enviarRutinasAlReloj()
                }

                else -> {
                    Log.w(TAG, "Ruta desconocida: $path")
                }
            }
        }
    }

    /**
     * Busca la rutina por nombre en la API real (ya no en el
     * catálogo local simulado) para conseguir su id de verdad; con
     * ese id, EntrenamientoActivoActivity puede cargar sus
     * ejercicios reales igual que si el teléfono hubiera iniciado el
     * entrenamiento.
     */
    private fun abrirEntrenamientoDesdeReloj(nombreRutina: String) {

        val nombreFinal = nombreRutina.ifBlank { "Entrenamiento" }

        serviceScope.launch {

            val token = TokenManager(this@PhoneWearListenerService).obtenerBearer()

            val rutinaId = rutinasRepository.obtenerTodas(token)
                .getOrNull()
                ?.firstOrNull { it.esActivo && it.nombre.equals(nombreFinal, ignoreCase = true) }
                ?.id
                ?: 0

            if (rutinaId == 0) {
                Log.w(TAG, "No se encontró en la API ninguna rutina activa llamada \"$nombreFinal\"")
            }

            val intent = Intent(this@PhoneWearListenerService, EntrenamientoActivoActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            intent.putExtra("RUTINA_ID", rutinaId)
            intent.putExtra("RUTINA_NOMBRE", nombreFinal)
            intent.putExtra(
                EntrenamientoActivoActivity.EXTRA_LANZADO_DESDE_RELOJ,
                true
            )

            startActivity(intent)
        }
    }

    /**
     * Manda al reloj todas las rutinas activas con sus ejercicios
     * completos (nombre, series, repeticiones, descanso), para que
     * el reloj pueda correr una rutina de principio a fin sin
     * depender de datos simulados ni de que el teléfono esté al
     * mando en ese momento.
     *
     * Formato del mensaje (texto plano, una rutina por línea):
     *   id|nombre|nivel|ej1Nombre,ej1Series,ej1Reps,ej1Descanso;ej2...
     */
    private fun enviarRutinasAlReloj() {

        serviceScope.launch {

            val token = TokenManager(this@PhoneWearListenerService).obtenerBearer()

            val rutinasActivas = rutinasRepository.obtenerTodas(token)
                .getOrNull()
                ?.filter { it.esActivo }
                ?: emptyList()

            // La lista general puede no traer los ejercicios de cada
            // rutina incluidos, así que se pide el detalle completo
            // de cada una en paralelo (igual que hace
            // ProgramarEntrenamientoActivity al elegir una rutina).
            val rutinasCompletas = coroutineScope {
                rutinasActivas
                    .map { rutina -> async { rutinasRepository.obtenerPorId(token, rutina.id).getOrNull() } }
                    .awaitAll()
                    .filterNotNull()
            }

            val payload = rutinasCompletas.joinToString("\n") { rutina ->
                val ejerciciosBlob = rutina.ejercicios.joinToString(";") { ejercicio ->
                    "${ejercicio.nombre},${ejercicio.series},${ejercicio.repeticiones},${ejercicio.descanso}"
                }
                "${rutina.id}|${rutina.nombre}|${rutina.nivel}|$ejerciciosBlob"
            }

            Log.d(TAG, "Mandando ${rutinasCompletas.size} rutinas reales al reloj.")

            WatchMessageSender.sendMessage(
                context = this@PhoneWearListenerService,
                path = PATH_ROUTINES_LIST,
                message = payload
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}