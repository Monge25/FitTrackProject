package com.example.proyecto.ui.entrenamiento
import com.example.movilmensaje.R

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.util.Locale
import kotlin.random.Random
import com.example.proyecto.data.mock.RutinasCatalog
import com.example.proyecto.data.model.EjercicioProgramado
import com.example.proyecto.data.model.SesionEntrenamiento
import com.example.proyecto.data.repository.ProgresoRepository
import com.example.proyecto.data.repository.RutinasRepository
import com.example.proyecto.data.wear.EntrenamientoBridge
import com.example.proyecto.data.wear.WatchConstants
import com.example.proyecto.data.wear.WatchMessageSender
import com.example.proyecto.utils.TokenManager
import androidx.lifecycle.lifecycleScope
import android.widget.Toast
import kotlinx.coroutines.launch
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable

class EntrenamientoActivoActivity :
    AppCompatActivity(),
    EntrenamientoBridge.Listener,
    MessageClient.OnMessageReceivedListener {

    private var sesionId: Int = -1
    private var ejerciciosCompletadosIds: List<Int> = emptyList()

    private lateinit var tvNombreRutina: TextView
    private lateinit var tvEjercicioActual: TextView
    private lateinit var tvSerieActual: TextView
    private lateinit var tvRepeticiones: TextView
    private lateinit var tvCronometro: TextView
    private lateinit var tvFrecuenciaCardiaca: TextView
    private lateinit var tvEstadoEntrenamiento: TextView

    private lateinit var tvProgresoEjercicios: TextView
    private lateinit var tvPorcentajeRutina: TextView
    private lateinit var progressRutina: ProgressBar
    private lateinit var tvSiguienteEjercicio: TextView

    private lateinit var vPuntoWatch: View
    private lateinit var tvEstadoWatch: TextView

    private lateinit var btnCompletarSerie: MaterialButton
    private lateinit var btnOmitirDescanso: MaterialButton
    private lateinit var btnPausar: MaterialButton
    private lateinit var btnFinalizar: MaterialButton

    private var segundosTranscurridos = 0
    private var entrenamientoActivo = true

    private var serieActual = 1
    private val totalSeries = 4

    private var indiceEjercicio = 0

    private var nombreRutinaActual = "Entrenamiento"

    // Se llenan en cargarRutina(), a partir de la rutina real elegida
    // (RutinasCatalog, buscada por RUTINA_ID). Si la rutina no tiene
    // ejercicios predeterminados (por ejemplo "Cardio", o una rutina
    // personalizada), se usa un solo ejercicio genérico como respaldo
    // para no dejar la pantalla sin nada que mostrar.
    private var ejercicios: List<EjercicioProgramado> = emptyList()

    private val handlerCronometro =
        Handler(Looper.getMainLooper())

    private val handlerFrecuencia =
        Handler(Looper.getMainLooper())

    private var temporizadorDescanso:
            CountDownTimer? = null


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_entrenamiento_activo
        )

        inicializarComponentes()

        cargarRutina()

        configurarEventos()

        fechaInicioEntrenamiento = System.currentTimeMillis()

        iniciarCronometro()

        iniciarFrecuenciaCardiaca()

        val lanzadoDesdeReloj =
            intent.getBooleanExtra(
                EXTRA_LANZADO_DESDE_RELOJ,
                false
            )

        if (lanzadoDesdeReloj) {
            // El reloj ya inició el entrenamiento por su cuenta y fue
            // quien abrió esta pantalla; no hace falta decirle de
            // nuevo que está "listo" ni que "inicie".
            tvEstadoWatch.text = "CONECTADO"
            vPuntoWatch.setBackgroundResource(R.drawable.fondo_punto_online)
        } else {
            // Enlace con el reloj: le avisamos que hay un
            // entrenamiento listo y arrancamos de inmediato (esta
            // pantalla no tiene un paso separado de "confirmar
            // inicio").
            enviarAlReloj(path = WatchConstants.PATH_WORKOUT_READY, mensaje = nombreRutinaActual) {
                enviarAlReloj(path = WatchConstants.PATH_START_WORKOUT, mensaje = nombreRutinaActual)
            }

            verificarConexionReloj()
        }
    }

    override fun onResume() {
        super.onResume()
        EntrenamientoBridge.registrar(this)

        // Respaldo en vivo mientras esta pantalla está abierta —
        // mismo patrón que ya usa el reloj (MainActivity.kt,
        // fittrackwear), que resultó mucho más confiable que
        // depender solo del WearableListenerService en segundo
        // plano (PhoneWearListenerService) en dispositivos físicos.
        Wearable.getMessageClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        EntrenamientoBridge.quitar(this)
        Wearable.getMessageClient(this).removeListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val path = messageEvent.path

        mainHandlerReloj.post {
            when (path) {
                WatchConstants.PATH_PAUSE_WORKOUT -> onPausarDesdeReloj()
                WatchConstants.PATH_RESUME_WORKOUT -> onReanudarDesdeReloj()
                WatchConstants.PATH_FINISH_WORKOUT -> onFinalizarDesdeReloj()
            }
        }
    }

    private val mainHandlerReloj = Handler(Looper.getMainLooper())

    // --- EntrenamientoBridge.Listener: acciones disparadas desde el reloj ---

    override fun onPausarDesdeReloj() {
        if (entrenamientoActivo) {
            aplicarPausa()
        }
    }

    override fun onReanudarDesdeReloj() {
        if (!entrenamientoActivo) {
            aplicarReanudacion()
        }
    }

    override fun onFinalizarDesdeReloj() {
        finalizarEntrenamiento(avisarAlReloj = false)
    }

    private var fechaInicioEntrenamiento: Long = 0L

    private var seriesCompletadasTotal: Int = 0

    // Se pone en true justo al empezar un descanso y en false al
    // terminarlo — el cronómetro general se detiene mientras tanto,
    // igual que ya hace el reloj (WorkoutTimer.pause() antes del
    // descanso). Antes el cronómetro nunca se detenía durante el
    // descanso, aunque sí lo hacía al pausar manualmente.
    private var enDescanso = false


    private fun inicializarComponentes() {

        tvNombreRutina =
            findViewById(
                R.id.tvNombreRutinaEntrenamiento
            )

        tvEjercicioActual =
            findViewById(
                R.id.tvEjercicioActual
            )

        tvSerieActual =
            findViewById(
                R.id.tvSerieActual
            )

        tvRepeticiones =
            findViewById(
                R.id.tvRepeticiones
            )

        tvCronometro =
            findViewById(
                R.id.tvCronometro
            )

        tvFrecuenciaCardiaca =
            findViewById(
                R.id.tvFrecuenciaCardiaca
            )

        tvEstadoEntrenamiento =
            findViewById(
                R.id.tvEstadoEntrenamiento
            )

        tvProgresoEjercicios =
            findViewById(
                R.id.tvProgresoEjercicios
            )

        tvPorcentajeRutina =
            findViewById(
                R.id.tvPorcentajeRutina
            )

        progressRutina =
            findViewById(
                R.id.progressRutina
            )

        tvSiguienteEjercicio =
            findViewById(
                R.id.tvSiguienteEjercicio
            )

        vPuntoWatch =
            findViewById(
                R.id.vPuntoWatchEntrenamiento
            )

        tvEstadoWatch =
            findViewById(
                R.id.tvEstadoWatchEntrenamiento
            )

        btnCompletarSerie =
            findViewById(
                R.id.btnCompletarSerie
            )

        btnOmitirDescanso =
            findViewById(
                R.id.btnOmitirDescanso
            )

        btnPausar =
            findViewById(
                R.id.btnPausarEntrenamiento
            )

        btnFinalizar =
            findViewById(
                R.id.btnFinalizarEntrenamiento
            )

        // Se habilita en continuarTrasCargarRutina(), una vez que
        // `ejercicios` ya tiene datos reales (evita un click antes
        // de que responda la API).
        btnCompletarSerie.isEnabled = false
    }


    private fun cargarRutina() {
        val rutinaId     = intent.getIntExtra("RUTINA_ID", -1)
        val nombreRutina = intent.getStringExtra("RUTINA_NOMBRE") ?: "Entrenamiento"

        nombreRutinaActual  = nombreRutina
        tvNombreRutina.text = nombreRutina.uppercase()

        if (rutinaId == -1) {
            ejercicios = listOf(
                EjercicioProgramado(
                    nombre           = nombreRutina,
                    series           = 1,
                    repeticiones     = 0,
                    descansoSegundos = 30
                )
            )
            mostrarEjercicioActual()
            btnCompletarSerie.isEnabled = true
            return
        }

        lifecycleScope.launch {
            val token = TokenManager(this@EntrenamientoActivoActivity).obtenerBearer()

            RutinasRepository().obtenerPorId(token, rutinaId).fold(
                onSuccess = { rutina ->
                    ejercicios = if (rutina.ejercicios.isEmpty()) {
                        listOf(
                            EjercicioProgramado(
                                nombre           = nombreRutina,
                                series           = 1,
                                repeticiones     = 0,
                                descansoSegundos = 30
                            )
                        )
                    } else {
                        rutina.ejercicios.map { ej ->
                            EjercicioProgramado(
                                id               = ej.id.toLong(),
                                nombre           = ej.nombre,
                                series           = ej.series,
                                repeticiones     = ej.repeticiones,
                                pesoKg           = ej.peso?.toFloat() ?: 0f,
                                descansoSegundos = ej.descanso,
                                notas            = ej.notas ?: ""
                            )
                        }
                    }

                    mostrarEjercicioActual()
                    btnCompletarSerie.isEnabled = true

                    // Iniciar sesión en la API
                    val sesionIdExtra = intent.getIntExtra("SESION_ID", -1)
                    if (sesionIdExtra != -1) {
                        sesionId = sesionIdExtra
                        val sesionesRepo = com.example.proyecto.data.repository.SesionesRepository()

                        sesionesRepo.iniciar(token, sesionId)

                        sesionesRepo.obtenerPorId(token, sesionId).fold(
                            onSuccess = { sesion ->
                                ejerciciosCompletadosIds = sesion.ejercicios.map { it.id }
                            },
                            onFailure = { }
                        )
                    }
                },
                onFailure = {
                    ejercicios = listOf(
                        EjercicioProgramado(
                            nombre           = nombreRutina,
                            series           = 1,
                            repeticiones     = 0,
                            descansoSegundos = 30
                        )
                    )
                    mostrarEjercicioActual()
                    btnCompletarSerie.isEnabled = true
                }
            )
        }
    }


    private fun configurarEventos() {

        btnCompletarSerie.setOnClickListener {

            completarSerie()
        }

        btnOmitirDescanso.setOnClickListener {

            omitirDescanso()
        }

        btnPausar.setOnClickListener {

            cambiarEstadoEntrenamiento()
        }

        btnFinalizar.setOnClickListener {

            finalizarEntrenamiento()
        }
    }


    private fun mostrarEjercicioActual() {

        val ejercicio =
            ejercicios[indiceEjercicio]

        tvEjercicioActual.text =
            ejercicio.nombre

        tvSerieActual.text =
            "SERIE $serieActual / ${ejercicio.series}"

        tvRepeticiones.text =
            "${ejercicio.repeticiones} REPETICIONES"

        tvEstadoEntrenamiento.text =
            "ENTRENAMIENTO ACTIVO"

        btnCompletarSerie.text =
            "COMPLETAR SERIE"

        tvProgresoEjercicios.text =
            "Ejercicio ${indiceEjercicio + 1} de ${ejercicios.size}"

        val siguiente =
            ejercicios.getOrNull(indiceEjercicio + 1)

        tvSiguienteEjercicio.text =
            siguiente?.nombre ?: "Último ejercicio"

        val porcentaje =
            (
                    (indiceEjercicio.toFloat() /
                            ejercicios.size.toFloat()) * 100
                    ).toInt()

        tvPorcentajeRutina.text = "$porcentaje%"

        progressRutina.progress = porcentaje
    }


    private fun completarSerie() {
        seriesCompletadasTotal++

        val ejercicio = ejercicios[indiceEjercicio]

        // Notificar al reloj
        enviarAlReloj(
            path    = WatchConstants.PATH_REST,
            mensaje = ejercicio.descansoSegundos.toString()
        )

        // Marcar en la API si es la última serie del ejercicio
        val esUltimaSerie = serieActual >= ejercicio.series
        if (esUltimaSerie && sesionId != -1) {
            val ejCompletadoId = ejerciciosCompletadosIds.getOrNull(indiceEjercicio)
            if (ejCompletadoId != null) {
                lifecycleScope.launch {
                    val token = com.example.proyecto.utils.TokenManager(
                        this@EntrenamientoActivoActivity
                    ).obtenerBearer()

                    com.example.proyecto.data.repository.SesionesRepository()
                        .completarEjercicio(
                            token          = token,
                            sesionId       = sesionId,
                            ejCompletadoId = ejCompletadoId,
                            request        = com.example.proyecto.data.model.CompletarEjercicioRequest(
                                seriesCompletadas       = ejercicio.series,
                                repeticionesCompletadas = ejercicio.repeticiones,
                                pesoUsado               = if (ejercicio.pesoKg > 0f)
                                    ejercicio.pesoKg.toDouble()
                                else null,
                                notas                   = ejercicio.notas.ifBlank { null }
                            )
                        )
                }
            }
        }

        iniciarDescanso(ejercicio.descansoSegundos)
    }


    private fun iniciarDescanso(
        segundos: Int
    ) {

        btnCompletarSerie.isEnabled = false

        enDescanso = true

        btnOmitirDescanso.visibility = View.VISIBLE

        temporizadorDescanso?.cancel()

        temporizadorDescanso =

            object : CountDownTimer(
                segundos * 1000L,
                1000L
            ) {

                override fun onTick(
                    millisUntilFinished: Long
                ) {

                    val segundosRestantes =
                        millisUntilFinished / 1000

                    tvEstadoEntrenamiento.text =
                        "DESCANSO"

                    btnCompletarSerie.text =
                        "DESCANSO ${segundosRestantes}s"
                }


                override fun onFinish() {

                    enDescanso = false

                    btnOmitirDescanso.visibility = View.GONE

                    avanzarSerie()

                    btnCompletarSerie.isEnabled =
                        true
                }

            }.start()
    }


    private fun omitirDescanso() {

        temporizadorDescanso?.cancel()

        enDescanso = false

        btnOmitirDescanso.visibility = View.GONE

        btnCompletarSerie.isEnabled = true

        avanzarSerie()

        // El reloj tiene su propio cronómetro de descanso corriendo
        // por su cuenta con la misma duración; se le avisa que ya
        // se reanudó para que no se quede contando de más.
        enviarAlReloj(
            path = WatchConstants.PATH_RESUME_WORKOUT,
            mensaje = "RESUME"
        )
    }


    private fun avanzarSerie() {

        val ejercicio =
            ejercicios[indiceEjercicio]

        if (
            serieActual < ejercicio.series
        ) {

            serieActual++

            mostrarEjercicioActual()

            enviarAlReloj(
                WatchConstants.PATH_UPDATE_WORKOUT,
                armarMensajeEntrenamiento()
            )

        } else {

            avanzarEjercicio()
        }
    }


    private fun avanzarEjercicio() {

        if (
            indiceEjercicio <
            ejercicios.lastIndex
        ) {

            indiceEjercicio++

            serieActual = 1

            mostrarEjercicioActual()

            enviarAlReloj(
                WatchConstants.PATH_NEXT_EXERCISE,
                armarMensajeEntrenamiento()
            )

        } else {

            finalizarEntrenamiento()
        }
    }


    private fun iniciarCronometro() {

        handlerCronometro.post(
            object : Runnable {

                override fun run() {

                    if (entrenamientoActivo && !enDescanso) {

                        segundosTranscurridos++

                        actualizarCronometro()
                    }

                    handlerCronometro.postDelayed(
                        this,
                        1000
                    )
                }
            }
        )
    }


    private fun actualizarCronometro() {

        val horas =
            segundosTranscurridos / 3600

        val minutos =
            (segundosTranscurridos % 3600) / 60

        val segundos =
            segundosTranscurridos % 60

        tvCronometro.text =
            String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                horas,
                minutos,
                segundos
            )
    }


    private fun iniciarFrecuenciaCardiaca() {

        handlerFrecuencia.post(
            object : Runnable {

                override fun run() {

                    if (entrenamientoActivo) {

                        val frecuencia =
                            Random.nextInt(
                                110,
                                151
                            )

                        tvFrecuenciaCardiaca.text =
                            "$frecuencia BPM"
                    }

                    handlerFrecuencia.postDelayed(
                        this,
                        3000
                    )
                }
            }
        )
    }


    private fun cambiarEstadoEntrenamiento() {

        if (entrenamientoActivo) {

            aplicarPausa()

            enviarAlReloj(WatchConstants.PATH_PAUSE_WORKOUT)

        } else {

            aplicarReanudacion()

            enviarAlReloj(WatchConstants.PATH_RESUME_WORKOUT)
        }
    }

    private fun aplicarPausa() {

        entrenamientoActivo = false

        btnPausar.text =
            "REANUDAR"

        tvEstadoEntrenamiento.text =
            "ENTRENAMIENTO PAUSADO"
    }

    private fun aplicarReanudacion() {

        entrenamientoActivo = true

        btnPausar.text =
            "PAUSAR"

        tvEstadoEntrenamiento.text =
            "ENTRENAMIENTO ACTIVO"
    }


    private fun finalizarEntrenamiento(avisarAlReloj: Boolean = true) {
        entrenamientoActivo = false
        temporizadorDescanso?.cancel()
        handlerCronometro.removeCallbacksAndMessages(null)
        handlerFrecuencia.removeCallbacksAndMessages(null)

        if (avisarAlReloj) {
            enviarAlReloj(WatchConstants.PATH_FINISH_WORKOUT)
        }

        val idSesion = guardarSesionEntrenamiento()

        if (sesionId != -1) {
            lifecycleScope.launch {
                val token = com.example.proyecto.utils.TokenManager(
                    this@EntrenamientoActivoActivity
                ).obtenerBearer()

                com.example.proyecto.data.repository.SesionesRepository()
                    .finalizar(token, sesionId)

                // Navegar DESPUÉS de que finalice la llamada a la API
                val intent = Intent(
                    this@EntrenamientoActivoActivity,
                    ResumenEntrenamientoActivity::class.java
                )
                intent.putExtra(ResumenEntrenamientoActivity.EXTRA_SESION_ID, idSesion)
                startActivity(intent)
                finish()
            }
        } else {
            val intent = Intent(this, ResumenEntrenamientoActivity::class.java)
            intent.putExtra(ResumenEntrenamientoActivity.EXTRA_SESION_ID, idSesion)
            startActivity(intent)
            finish()
        }
    }

    private fun guardarSesionEntrenamiento(): Long {

        val fechaFin =
            System.currentTimeMillis()

        val rutinaId =
            intent.getIntExtra(
                "RUTINA_ID",
                0
            )

        val nombreRutina =
            intent.getStringExtra(
                "RUTINA_NOMBRE"
            ) ?: "Entrenamiento"

        val sesion =
            SesionEntrenamiento(

                rutinaId = rutinaId,

                nombreRutina = nombreRutina,

                fechaInicio = fechaInicioEntrenamiento,

                fechaFin = fechaFin,

                duracionSegundos = segundosTranscurridos,

                ejerciciosCompletados = ejercicios.size,

                seriesCompletadas = seriesCompletadasTotal,

                frecuenciaPromedio =
                    tvFrecuenciaCardiaca.text
                        .toString()
                        .replace(" BPM", "")
                        .toIntOrNull()
                        ?: 120
            )

        ProgresoRepository(this)
            .guardarSesion(
                sesion
            )

        return sesion.id
    }


    /**
     * Arma el mensaje en el formato que espera
     * WearListenerService.parseWorkout() del lado del reloj:
     * "nombreRutina|ejercicioActual|siguienteEjercicio|serieActual|totalSeries|repeticiones|totalEjercicios|duracionMinutos"
     */
    private fun armarMensajeEntrenamiento(): String {

        val ejercicio = ejercicios[indiceEjercicio]

        val siguiente =
            ejercicios.getOrNull(indiceEjercicio + 1)?.nombre
                ?: "Último ejercicio"

        return listOf(
            nombreRutinaActual,
            ejercicio.nombre,
            siguiente,
            serieActual,
            ejercicio.series,
            ejercicio.repeticiones,
            ejercicios.size,
            45
        ).joinToString("|")
    }

    private fun enviarAlReloj(
        path: String,
        mensaje: String = armarMensajeEntrenamiento(),
        onSent: () -> Unit = {}
    ) {
        WatchMessageSender.sendMessage(
            context = this,
            path = path,
            message = mensaje,
            onSent = onSent
        )
    }

    private fun verificarConexionReloj() {

        Wearable.getNodeClient(this)
            .connectedNodes
            .addOnSuccessListener { nodos ->

                if (nodos.isNotEmpty()) {

                    tvEstadoWatch.text = "CONECTADO"

                    vPuntoWatch.setBackgroundResource(
                        R.drawable.fondo_punto_online
                    )

                } else {

                    tvEstadoWatch.text = "SIN RELOJ"

                    vPuntoWatch.setBackgroundResource(
                        R.drawable.fondo_punto_offline
                    )
                }
            }
            .addOnFailureListener {

                tvEstadoWatch.text = "SIN RELOJ"

                vPuntoWatch.setBackgroundResource(
                    R.drawable.fondo_punto_offline
                )
            }
    }


    override fun onDestroy() {

        super.onDestroy()

        handlerCronometro.removeCallbacksAndMessages(
            null
        )

        handlerFrecuencia.removeCallbacksAndMessages(
            null
        )

        temporizadorDescanso?.cancel()
    }

    companion object {
        const val EXTRA_LANZADO_DESDE_RELOJ = "EXTRA_LANZADO_DESDE_RELOJ"
    }
}