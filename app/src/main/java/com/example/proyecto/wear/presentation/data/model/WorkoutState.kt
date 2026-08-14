package com.example.proyecto.wear.presentation.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object WorkoutState {

    var currentScreen by mutableStateOf(WorkoutScreen.SPLASH)
        private set

    var phoneConnected by mutableStateOf(false)
        private set

    var workout by mutableStateOf(Entrenamiento())
        private set

    var elapsedSeconds by mutableLongStateOf(0L)
        private set

    var restSeconds by mutableIntStateOf(45)
        private set

    var heartRate by mutableIntStateOf(110)
        private set

    var calories by mutableIntStateOf(0)
        private set

    // Total de series completadas en todo el entrenamiento (no se
    // reinicia al pasar de ejercicio, solo al empezar uno nuevo) —
    // para que el resumen final muestre el mismo dato que ya
    // muestra el teléfono ("Series completadas").
    var seriesCompletadas by mutableIntStateOf(0)
        private set

    var sesionHistorialSeleccionada by mutableStateOf<HistorialEntrenamiento?>(null)
        private set

    // Rutinas reales (de la API, vía el teléfono) para elegir en
    // "Ver rutinas". Se piden cada vez que se entra a esa pantalla.
    var rutinasRemotas by mutableStateOf<List<RutinaRemota>>(emptyList())
        private set

    var cargandoRutinas by mutableStateOf(false)
        private set

    // Lista completa de ejercicios de la rutina real elegida (solo
    // se llena cuando la rutina la eligió el propio reloj — ver
    // seleccionarRutinaRemota). Cuando el entrenamiento lo maneja el
    // teléfono, el reloj no conoce la lista completa y esto se queda
    // vacío; MainActivity usa eso para decidir qué lógica de avance
    // (y qué descanso real) aplicar.
    var ejerciciosRutinaActual: List<EjercicioRemoto> = emptyList()
        private set

    var indiceEjercicioActual: Int = 0
        private set

    fun setConnected(connected: Boolean) {
        phoneConnected = connected
    }

    fun showRoutineList() {
        currentScreen = WorkoutScreen.ROUTINE_LIST
    }

    fun mostrarCargandoRutinas() {
        cargandoRutinas = true
        rutinasRemotas = emptyList()
    }

    fun actualizarRutinasRemotas(lista: List<RutinaRemota>) {
        rutinasRemotas = lista
        cargandoRutinas = false
    }

    /** Rutina elegida de la lista real que mandó el teléfono. */
    fun seleccionarRutinaRemota(rutina: RutinaRemota) {

        ejerciciosRutinaActual = rutina.ejercicios
        indiceEjercicioActual = 0

        val primero = rutina.ejercicios.firstOrNull()
        val segundo = rutina.ejercicios.getOrNull(1)

        val entrenamiento = Entrenamiento(
            nombreRutina = rutina.nombre,
            ejercicioActual = primero?.nombre ?: rutina.nombre,
            siguienteEjercicio = segundo?.nombre ?: "Último ejercicio",
            serieActual = 1,
            totalSeries = primero?.series ?: 1,
            repeticiones = primero?.repeticiones ?: 0,
            totalEjercicios = rutina.ejercicios.size.coerceAtLeast(1),
            duracionEstimadaMinutos = 0
        )

        prepareWorkout(entrenamiento)
    }

    fun avanzarIndiceEjercicio(nuevoIndice: Int) {
        indiceEjercicioActual = nuevoIndice
    }

    /** El teléfono es quien maneja el entrenamiento: se limpia cualquier rutina remota anterior. */
    fun limpiarListaEjerciciosStandalone() {
        ejerciciosRutinaActual = emptyList()
        indiceEjercicioActual = 0
    }

    fun selectRoutine(entrenamiento: Entrenamiento) {
        prepareWorkout(entrenamiento)
    }

    fun prepareWorkout(entrenamiento: Entrenamiento = Entrenamiento()) {
        workout = entrenamiento
        elapsedSeconds = 0L
        restSeconds = 45
        heartRate = 110
        calories = 0
        seriesCompletadas = 0
        currentScreen = WorkoutScreen.READY
    }

    fun registrarSerieCompletada() {
        seriesCompletadas++
    }

    fun startWorkout() {
        currentScreen = WorkoutScreen.ACTIVE
    }

    fun pauseWorkout() {
        currentScreen = WorkoutScreen.PAUSED
    }

    fun resumeWorkout() {
        currentScreen = WorkoutScreen.ACTIVE
    }

    fun startRest(seconds: Int = 45) {
        restSeconds = seconds
        currentScreen = WorkoutScreen.REST
    }

    fun finishRest() {
        currentScreen = WorkoutScreen.ACTIVE
    }

    fun finishWorkout() {
        currentScreen = WorkoutScreen.FINISHED
    }

    fun showHistory() {
        sesionHistorialSeleccionada = null
        currentScreen = WorkoutScreen.HISTORY
    }

    fun showHistoryDetail(sesion: HistorialEntrenamiento) {
        sesionHistorialSeleccionada = sesion
        currentScreen = WorkoutScreen.HISTORY_DETAIL
    }

    fun showHome() {
        currentScreen = WorkoutScreen.HOME
    }

    fun updateWorkout(entrenamiento: Entrenamiento) {
        workout = entrenamiento
    }

    fun updateElapsedTime(seconds: Long) {
        elapsedSeconds = seconds
        calories = (seconds / 8L).toInt()
    }

    fun updateRestTime(seconds: Int) {
        restSeconds = seconds
    }

    fun updateHeartRate(value: Int) {
        heartRate = value
    }
}