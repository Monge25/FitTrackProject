package com.example.proyecto.wear.presentation.communication

import android.util.Log
import com.example.proyecto.wear.presentation.data.model.Entrenamiento
import com.example.proyecto.wear.presentation.data.model.WorkoutState
import com.example.proyecto.wear.presentation.service.HeartRateSimulator
import com.example.proyecto.wear.presentation.service.RestTimer
import com.example.proyecto.wear.presentation.service.WorkoutTimer

/**
 * Lógica real de qué hacer con cada mensaje que llega del teléfono.
 * La usan tanto WearListenerService (recepción en segundo plano, vía
 * el Service declarado en el manifest) como MainActivity (recepción
 * directa mientras la app está abierta, vía MessageClient.addListener,
 * el mismo patrón que en el proyecto de clase "miHolaWear" y que
 * resultó ser mucho más confiable en emuladores).
 */
object WorkoutMessageHandler {

    private const val TAG = "WorkoutMessageHandler"

    fun handle(path: String, message: String) {

        Log.d(TAG, "Procesando mensaje. Ruta: $path, contenido: $message")

        when (path) {

            WearConstants.PATH_CONNECTION -> {
                WorkoutState.setConnected(true)
            }

            WearConstants.PATH_WORKOUT_READY -> {
                val workout = parseWorkout(message)
                WorkoutState.prepareWorkout(workout)
            }

            WearConstants.PATH_START_WORKOUT -> {
                WorkoutState.startWorkout()
                WorkoutTimer.start()
                HeartRateSimulator.start()
            }

            WearConstants.PATH_PAUSE_WORKOUT -> {
                WorkoutTimer.pause()
                WorkoutState.pauseWorkout()
            }

            WearConstants.PATH_RESUME_WORKOUT -> {
                WorkoutTimer.resume()
                WorkoutState.resumeWorkout()
            }

            WearConstants.PATH_REST -> {
                WorkoutTimer.pause()
                WorkoutState.startRest(45)
                RestTimer.start(45)
            }

            WearConstants.PATH_NEXT_EXERCISE -> {
                val updatedWorkout = parseWorkout(message)
                WorkoutState.updateWorkout(updatedWorkout)
                WorkoutState.startWorkout()
            }

            WearConstants.PATH_UPDATE_WORKOUT -> {
                WorkoutState.updateWorkout(parseWorkout(message))
            }

            WearConstants.PATH_FINISH_WORKOUT -> {
                WorkoutTimer.pause()
                RestTimer.stop()
                HeartRateSimulator.stop()
                WorkoutState.finishWorkout()
            }

            else -> {
                Log.w(TAG, "Ruta desconocida: $path")
            }
        }
    }

    /*
     * Formato esperado:
     *
     * Push Day|Press de banca|Press militar|2|4|10|6|45
     */
    private fun parseWorkout(message: String): Entrenamiento {
        if (message.isBlank()) {
            return Entrenamiento()
        }

        return try {
            val values = message.split("|")

            Entrenamiento(
                nombreRutina = values.getOrNull(0) ?: "Entrenamiento",
                ejercicioActual = values.getOrNull(1) ?: "Ejercicio",
                siguienteEjercicio = values.getOrNull(2) ?: "Siguiente ejercicio",
                serieActual = values.getOrNull(3)?.toIntOrNull() ?: 1,
                totalSeries = values.getOrNull(4)?.toIntOrNull() ?: 4,
                repeticiones = values.getOrNull(5)?.toIntOrNull() ?: 10,
                totalEjercicios = values.getOrNull(6)?.toIntOrNull() ?: 1,
                duracionEstimadaMinutos =
                    values.getOrNull(7)?.toIntOrNull() ?: 45
            )
        } catch (exception: Exception) {
            Log.e(TAG, "No se pudo interpretar el entrenamiento.", exception)
            Entrenamiento()
        }
    }
}