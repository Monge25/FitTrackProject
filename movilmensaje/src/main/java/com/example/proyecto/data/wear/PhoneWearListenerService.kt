package com.example.proyecto.data.wear

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.proyecto.data.mock.RutinasCatalog
import com.example.proyecto.ui.entrenamiento.EntrenamientoActivoActivity
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

/**
 * Recibe los mensajes que manda el módulo del reloj (fittrackwear)
 * a través de la Wearable Data Layer API.
 *
 * El reloj manda estos paths (ver WearConstants.kt en fittrackwear):
 *  - PATH_START_WORKOUT (con el nombre de la rutina como contenido)
 *  - PATH_PAUSE_WORKOUT
 *  - PATH_RESUME_WORKOUT
 *  - PATH_FINISH_WORKOUT
 *
 * Se ejecuta en segundo plano, fuera del ciclo de vida de cualquier
 * Activity. PAUSE/RESUME/FINISH se reenvían a EntrenamientoBridge —
 * si EntrenamientoActivoActivity ya está abierta (porque el
 * entrenamiento lo inició el teléfono), actualiza su UI en tiempo
 * real. START es distinto: si el entrenamiento lo inició el reloj
 * (no el teléfono), en el teléfono no hay ninguna Activity abierta
 * todavía, así que aquí mismo se lanza EntrenamientoActivoActivity
 * con la rutina que mandó el reloj.
 */
class PhoneWearListenerService : WearableListenerService() {

    companion object {
        private const val TAG = "PhoneWearListener"

        // Deben coincidir exactamente con WearConstants.kt del módulo fittrackwear
        private const val PATH_START_WORKOUT = "/fittrack/workout/start"
        private const val PATH_PAUSE_WORKOUT = "/fittrack/workout/pause"
        private const val PATH_RESUME_WORKOUT = "/fittrack/workout/resume"
        private const val PATH_FINISH_WORKOUT = "/fittrack/workout/finish"
    }

    private val mainHandler = Handler(Looper.getMainLooper())

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

                else -> {
                    Log.w(TAG, "Ruta desconocida: $path")
                }
            }
        }
    }

    private fun abrirEntrenamientoDesdeReloj(nombreRutina: String) {

        val nombreFinal =
            nombreRutina.ifBlank { "Entrenamiento" }

        val rutinaId =
            RutinasCatalog.buscarPorNombre(nombreFinal)?.id ?: 0

        val intent = Intent(this, EntrenamientoActivoActivity::class.java)

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