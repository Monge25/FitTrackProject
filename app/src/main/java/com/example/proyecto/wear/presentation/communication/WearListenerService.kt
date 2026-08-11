package com.example.proyecto.wear.presentation.communication

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearListenerService : WearableListenerService() {

    companion object {
        private const val TAG = "WearListenerService"
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        val path = messageEvent.path
        val message = messageEvent.data.toString(Charsets.UTF_8)

        Log.d(TAG, "Mensaje recibido (Service). Ruta: $path, contenido: $message")

        mainHandler.post {
            WorkoutMessageHandler.handle(path, message)
        }
    }
}