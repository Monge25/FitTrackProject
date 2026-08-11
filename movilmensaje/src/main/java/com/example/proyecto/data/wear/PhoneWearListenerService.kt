package com.example.proyecto.data.wear

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

/**
 * Recibe los mensajes que manda el módulo del reloj (fittrackwear)
 * a través de la Wearable Data Layer API.
 *
 * El reloj manda estos paths (ver WearConstants.kt en fittrackwear):
 *  - PATH_START_WORKOUT
 *  - PATH_PAUSE_WORKOUT
 *  - PATH_RESUME_WORKOUT
 *  - PATH_FINISH_WORKOUT
 *
 * Por ahora solo se registran en el log. Si quieres que actualicen
 * la UI de EntrenamientoActivoActivity en tiempo real (por ejemplo,
 * pausar el cronómetro del teléfono cuando el usuario pausa desde el
 * reloj), lo más simple es exponer un objeto de estado compartido
 * (parecido a WorkoutState del reloj) y actualizarlo aquí, o usar
 * LocalBroadcastManager / un SharedFlow para notificar a la Activity.
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

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        val path = messageEvent.path
        val mensaje = messageEvent.data.toString(Charsets.UTF_8)

        Log.d(TAG, "Mensaje recibido del reloj. Ruta: $path, contenido: $mensaje")

        when (path) {

            PATH_START_WORKOUT -> {
                Log.d(TAG, "El reloj inició el entrenamiento.")
                // TODO: notificar a EntrenamientoActivoActivity si sigue abierta
            }

            PATH_PAUSE_WORKOUT -> {
                Log.d(TAG, "El reloj pausó el entrenamiento.")
                // TODO: pausar cronómetro/estado en el teléfono
            }

            PATH_RESUME_WORKOUT -> {
                Log.d(TAG, "El reloj reanudó el entrenamiento.")
                // TODO: reanudar cronómetro/estado en el teléfono
            }

            PATH_FINISH_WORKOUT -> {
                Log.d(TAG, "El reloj finalizó el entrenamiento.")
                // TODO: cerrar EntrenamientoActivoActivity y guardar la sesión
                // si el usuario finalizó el entrenamiento desde el reloj
            }

            else -> {
                Log.w(TAG, "Ruta desconocida: $path")
            }
        }
    }
}