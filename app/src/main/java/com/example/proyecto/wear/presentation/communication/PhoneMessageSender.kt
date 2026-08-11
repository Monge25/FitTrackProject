package com.example.proyecto.wear.presentation.communication

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable

object PhoneMessageSender {

    private const val TAG = "PhoneMessageSender"

    fun sendMessage(
        context: Context,
        path: String,
        message: String = ""
    ) {
        Wearable.getNodeClient(context)
            .connectedNodes
            .addOnSuccessListener { nodes ->

                if (nodes.isEmpty()) {
                    Log.w(TAG, "No hay teléfonos conectados.")
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
                    "No se pudieron obtener los dispositivos conectados.",
                    exception
                )
            }
    }
}