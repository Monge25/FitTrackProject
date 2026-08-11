package com.example.proyecto.wear.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.proyecto.wear.presentation.communication.PhoneMessageSender
import com.example.proyecto.wear.presentation.communication.WearConstants
import com.example.proyecto.wear.presentation.communication.WorkoutMessageHandler
import com.example.proyecto.wear.presentation.data.model.WorkoutScreen
import com.example.proyecto.wear.presentation.data.model.WorkoutState
import com.example.proyecto.wear.presentation.finished.WorkoutFinishedScreen
import com.example.proyecto.wear.presentation.history.WorkoutHistoryScreen
import com.example.proyecto.wear.presentation.home.HomeScreen
import com.example.proyecto.wear.presentation.data.model.RoutineCatalog
import com.example.proyecto.wear.presentation.data.model.HistorialEntrenamiento
import com.example.proyecto.wear.presentation.data.model.WorkoutHistoryStore
import com.example.proyecto.wear.presentation.paused.PausedWorkoutScreen
import com.example.proyecto.wear.presentation.ready.ReadyWorkoutScreen
import com.example.proyecto.wear.presentation.rest.RestScreen
import com.example.proyecto.wear.presentation.routines.RoutineListScreen
import com.example.proyecto.wear.presentation.service.HeartRateSimulator
import com.example.proyecto.wear.presentation.service.RestTimer
import com.example.proyecto.wear.presentation.service.WorkoutTimer
import com.example.proyecto.wear.presentation.splash.SplashScreen
import com.example.proyecto.wear.presentation.theme.ProyectoTheme
import com.example.proyecto.wear.presentation.utils.TimeUtils
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.example.proyecto.wear.presentation.workout.WorkoutScreen as ActiveWorkoutScreen

class MainActivity :
    ComponentActivity(),
    MessageClient.OnMessageReceivedListener {

    companion object {
        private const val ULTIMO_EJERCICIO_MARCADOR = "Último ejercicio"
        private const val TAG = "MainActivity"
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verificarConexionTelefono()

        setContent {
            ProyectoTheme {
                FitTrackWearApp()
            }
        }
    }

    // Escucha activa mientras la app está abierta y visible — mismo
    // patrón que en "miHolaWear". No reemplaza a WearListenerService
    // (que sigue funcionando en segundo plano), es un respaldo que
    // resultó ser más confiable al probar en emuladores.
    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getMessageClient(this).removeListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {

        val path = messageEvent.path
        val message = messageEvent.data.toString(Charsets.UTF_8)

        android.util.Log.d(
            TAG,
            "Mensaje recibido (Activity). Ruta: $path, contenido: $message"
        )

        mainHandler.post {
            WorkoutMessageHandler.handle(path, message)
        }
    }

    private fun verificarConexionTelefono() {
        Wearable.getNodeClient(this)
            .connectedNodes
            .addOnSuccessListener { nodos ->
                WorkoutState.setConnected(nodos.isNotEmpty())
            }
            .addOnFailureListener {
                WorkoutState.setConnected(false)
            }
    }

    @Composable
    private fun FitTrackWearApp() {
        when (WorkoutState.currentScreen) {

            WorkoutScreen.SPLASH -> {
                SplashScreen(
                    onFinished = {
                        WorkoutState.showHome()
                    }
                )
            }

            WorkoutScreen.HOME -> {
                HomeScreen(
                    onOpenHistory = {
                        WorkoutState.showHistory()
                    },
                    onSeeRoutines = {
                        WorkoutState.showRoutineList()
                    }
                )
            }

            WorkoutScreen.ROUTINE_LIST -> {
                RoutineListScreen(
                    routines = RoutineCatalog.routines,
                    onSelectRoutine = { entrenamiento ->
                        WorkoutState.selectRoutine(entrenamiento)
                    },
                    onBack = {
                        WorkoutState.showHome()
                    }
                )
            }

            WorkoutScreen.READY -> {
                ReadyWorkoutScreen(
                    workout = WorkoutState.workout,
                    onStartWorkout = {
                        iniciarEntrenamiento()
                    },
                    onCancel = {
                        WorkoutState.showRoutineList()
                    }
                )
            }

            WorkoutScreen.ACTIVE -> {
                ActiveWorkoutScreen(
                    workout = WorkoutState.workout,
                    elapsedSeconds = WorkoutState.elapsedSeconds,
                    heartRate = WorkoutState.heartRate,
                    onCompleteSeries = {
                        completarSerie()
                    },
                    onPause = {
                        pausarEntrenamiento()
                    },
                    onFinish = {
                        finalizarEntrenamiento()
                    }
                )
            }

            WorkoutScreen.REST -> {
                RestScreen(
                    restSeconds = WorkoutState.restSeconds,
                    nextExercise = WorkoutState.workout.siguienteEjercicio,
                    heartRate = WorkoutState.heartRate,
                    onSkipRest = {
                        terminarDescanso()
                    }
                )
            }

            WorkoutScreen.PAUSED -> {
                PausedWorkoutScreen(
                    elapsedSeconds = WorkoutState.elapsedSeconds,
                    onResume = {
                        reanudarEntrenamiento()
                    },
                    onFinish = {
                        finalizarEntrenamiento()
                    }
                )
            }

            WorkoutScreen.FINISHED -> {
                WorkoutFinishedScreen(
                    workoutName = WorkoutState.workout.nombreRutina,
                    elapsedSeconds = WorkoutState.elapsedSeconds,
                    exercises = WorkoutState.workout.totalEjercicios,
                    heartRate = WorkoutState.heartRate,
                    calories = WorkoutState.calories,
                    onAccept = {
                        reiniciarAplicacion()
                    }
                )
            }

            WorkoutScreen.HISTORY -> {
                WorkoutHistoryScreen(
                    sesiones = WorkoutHistoryStore.obtenerHistorial(this@MainActivity),
                    onBack = {
                        WorkoutState.showHome()
                    }
                )
            }
        }
    }

    private fun iniciarEntrenamiento() {
        WorkoutState.startWorkout()
        WorkoutTimer.start()
        HeartRateSimulator.start()

        enviarMensajeAlTelefono(
            path = WearConstants.PATH_START_WORKOUT,
            message = "START"
        )
    }

    private fun completarSerie() {
        val actual = WorkoutState.workout

        if (actual.serieActual < actual.totalSeries) {

            // Queda otra serie del mismo ejercicio: solo avanza el
            // contador y manda a descansar.
            WorkoutState.updateWorkout(
                actual.copy(serieActual = actual.serieActual + 1)
            )

            WorkoutTimer.pause()
            WorkoutState.startRest(45)
            RestTimer.start(45)

        } else if (actual.siguienteEjercicio != ULTIMO_EJERCICIO_MARCADOR &&
            actual.siguienteEjercicio != actual.ejercicioActual
        ) {

            // Se acabaron las series de este ejercicio: pasa al
            // siguiente. Ojo: el reloj en modo standalone solo
            // conoce el nombre del "siguiente" ejercicio (no una
            // lista completa como el catálogo del teléfono), así
            // que después de este ya no hay más para mostrar.
            WorkoutState.updateWorkout(
                actual.copy(
                    ejercicioActual = actual.siguienteEjercicio,
                    siguienteEjercicio = ULTIMO_EJERCICIO_MARCADOR,
                    serieActual = 1
                )
            )

            WorkoutTimer.pause()
            WorkoutState.startRest(45)
            RestTimer.start(45)

        } else {

            // Ya no hay más ejercicios conocidos: se termina solo.
            finalizarEntrenamiento()
        }
    }

    private fun pausarEntrenamiento() {
        WorkoutTimer.pause()
        WorkoutState.pauseWorkout()

        enviarMensajeAlTelefono(
            path = WearConstants.PATH_PAUSE_WORKOUT,
            message = "PAUSE"
        )
    }

    private fun reanudarEntrenamiento() {
        WorkoutTimer.resume()
        WorkoutState.resumeWorkout()

        enviarMensajeAlTelefono(
            path = WearConstants.PATH_RESUME_WORKOUT,
            message = "RESUME"
        )
    }

    private fun terminarDescanso() {
        RestTimer.stop()
        WorkoutState.finishRest()
        WorkoutTimer.resume()

        enviarMensajeAlTelefono(
            path = WearConstants.PATH_RESUME_WORKOUT,
            message = "RESUME"
        )
    }

    private fun finalizarEntrenamiento() {
        WorkoutTimer.pause()
        RestTimer.stop()
        HeartRateSimulator.stop()

        guardarSesionEnHistorial()

        WorkoutState.finishWorkout()

        enviarMensajeAlTelefono(
            path = WearConstants.PATH_FINISH_WORKOUT,
            message = "FINISH"
        )
    }

    private fun guardarSesionEnHistorial() {
        WorkoutHistoryStore.guardarSesion(
            context = this,
            sesion = HistorialEntrenamiento(
                nombreRutina = WorkoutState.workout.nombreRutina,
                duracion = TimeUtils.formatSeconds(WorkoutState.elapsedSeconds),
                ejercicios = WorkoutState.workout.totalEjercicios,
                frecuenciaPromedio = WorkoutState.heartRate,
                calorias = WorkoutState.calories
            )
        )
    }

    private fun reiniciarAplicacion() {
        WorkoutTimer.stop()
        RestTimer.stop()
        HeartRateSimulator.stop()
        WorkoutState.showHome()
    }

    private fun enviarMensajeAlTelefono(
        path: String,
        message: String
    ) {
        PhoneMessageSender.sendMessage(
            context = this,
            path = path,
            message = message
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            WorkoutTimer.stop()
            RestTimer.stop()
            HeartRateSimulator.stop()
        }
    }
}