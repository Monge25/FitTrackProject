package com.example.proyecto.wear.presentation.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object WorkoutState {

    var currentScreen by mutableStateOf(WorkoutScreen.HOME)
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

    fun setConnected(connected: Boolean) {
        phoneConnected = connected
    }

    fun showRoutineList() {
        currentScreen = WorkoutScreen.ROUTINE_LIST
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
        currentScreen = WorkoutScreen.READY
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
        currentScreen = WorkoutScreen.HISTORY
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