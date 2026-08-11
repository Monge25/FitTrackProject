package com.example.proyecto.data.wear

import android.content.Context
import android.provider.Telephony
import android.util.Log
import com.google.android.gms.wearable.Wearable

/**
 * Espejo de PhoneMessageSender.kt (fittrackwear), pero para mandar
 * mensajes del teléfono hacia el reloj.
 */
object WatchMessageSender {

    private const val TAG = "WatchMessageSender"

    fun sendMessage(
        context: Context,
        path: String,
        message: String = "",
        onSent: () -> Unit = {}
    ) {
        Wearable.getNodeClient(context)
            .connectedNodes
            .addOnSuccessListener { nodes ->

                if (nodes.isEmpty()) {
                    Log.w(TAG, "No hay reloj conectado.")
                    return@addOnSuccessListener
                }

                nodes.forEach { node ->
                    Wearable.getMessageClient(context)
                        .sendMessage(
                            node.id,
                            path,
                            message.toByteArray(Charsets.UTF_8)
                        )
                        .addOnSuccessListener {
                            Log.d(
                                TAG,
                                "Mensaje enviado a ${node.displayName}: $path"
                            )
                            onSent()
                        }
                        .addOnFailureListener { exception ->
                            Log.e(
                                TAG,
                                "Error al enviar el mensaje.",
                                exception
                            )
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    TAG,
                    "No se pudieron obtener los nodos conectados.",
                    exception
                )
            }
    }
}