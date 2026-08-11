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
import com.example.proyecto.data.model.SesionEntrenamiento
import com.example.proyecto.data.repository.ProgresoRepository
import com.example.proyecto.data.wear.WatchConstants
import com.example.proyecto.data.wear.WatchMessageSender
import com.google.android.gms.wearable.Wearable

class EntrenamientoActivoActivity : AppCompatActivity() {

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
    private lateinit var btnPausar: MaterialButton
    private lateinit var btnFinalizar: MaterialButton

    private var segundosTranscurridos = 0
    private var entrenamientoActivo = true

    private var serieActual = 1
    private val totalSeries = 4

    private var indiceEjercicio = 0

    private var nombreRutinaActual = "Entrenamiento"

    // TODO: hoy es una lista fija porque la pantalla de Rutinas
    // (RutinasFragment) también usa datos fijos y no hay endpoint de
    // ejercicios en el backend todavía. Cuando exista ese endpoint,
    // cargar esta lista desde la rutina real (RUTINA_ID) en vez de
    // dejarla hardcodeada aquí.
    private val ejercicios = listOf(
        EjercicioEntrenamiento(
            "Press de banca",
            4,
            10,
            60
        ),
        EjercicioEntrenamiento(
            "Press militar",
            4,
            12,
            60
        ),
        EjercicioEntrenamiento(
            "Fondos de tríceps",
            3,
            12,
            45
        )
    )

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

        mostrarEjercicioActual()

        configurarEventos()

        fechaInicioEntrenamiento = System.currentTimeMillis()

        iniciarCronometro()

        iniciarFrecuenciaCardiaca()

        // Enlace con el reloj: le avisamos que hay un entrenamiento
        // listo y arrancamos de inmediato (esta pantalla no tiene un
        // paso separado de "confirmar inicio").
        enviarAlReloj(WatchConstants.PATH_WORKOUT_READY) {
            enviarAlReloj(WatchConstants.PATH_START_WORKOUT)
        }

        verificarConexionReloj()
    }

    private var fechaInicioEntrenamiento: Long = 0L

    private var seriesCompletadasTotal: Int = 0


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

        btnPausar =
            findViewById(
                R.id.btnPausarEntrenamiento
            )

        btnFinalizar =
            findViewById(
                R.id.btnFinalizarEntrenamiento
            )
    }


    private fun cargarRutina() {

        val nombreRutina =
            intent.getStringExtra(
                "RUTINA_NOMBRE"
            ) ?: "Entrenamiento"

        nombreRutinaActual = nombreRutina

        tvNombreRutina.text =
            nombreRutina.uppercase()
    }


    private fun configurarEventos() {

        btnCompletarSerie.setOnClickListener {

            completarSerie()
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

        val ejercicio =
            ejercicios[indiceEjercicio]

        enviarAlReloj(WatchConstants.PATH_REST)

        iniciarDescanso(
            ejercicio.descanso
        )
    }


    private fun iniciarDescanso(
        segundos: Int
    ) {

        btnCompletarSerie.isEnabled = false

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

                    avanzarSerie()

                    btnCompletarSerie.isEnabled =
                        true
                }

            }.start()
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

                    if (entrenamientoActivo) {

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

        entrenamientoActivo =
            !entrenamientoActivo

        if (entrenamientoActivo) {

            btnPausar.text =
                "PAUSAR"

            tvEstadoEntrenamiento.text =
                "ENTRENAMIENTO ACTIVO"

            enviarAlReloj(WatchConstants.PATH_RESUME_WORKOUT)

        } else {

            btnPausar.text =
                "REANUDAR"

            tvEstadoEntrenamiento.text =
                "ENTRENAMIENTO PAUSADO"

            enviarAlReloj(WatchConstants.PATH_PAUSE_WORKOUT)
        }
    }


    private fun finalizarEntrenamiento() {

        entrenamientoActivo = false

        temporizadorDescanso?.cancel()

        handlerCronometro.removeCallbacksAndMessages(null)

        handlerFrecuencia.removeCallbacksAndMessages(null)

        enviarAlReloj(WatchConstants.PATH_FINISH_WORKOUT)

        val idSesion = guardarSesionEntrenamiento()

        val intent = Intent(this, ResumenEntrenamientoActivity::class.java)
        intent.putExtra(ResumenEntrenamientoActivity.EXTRA_SESION_ID, idSesion)
        startActivity(intent)

        finish()
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
}


data class EjercicioEntrenamiento(
    val nombre: String,
    val series: Int,
    val repeticiones: Int,
    val descanso: Int
)