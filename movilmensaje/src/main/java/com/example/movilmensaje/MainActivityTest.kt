package com.example.movilmensaje

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable

class MainActivityTest : AppCompatActivity(), MessageClient.OnMessageReceivedListener {
    lateinit var ctMensaje: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_test)

        ctMensaje = findViewById(R.id.ctMensaje)
        val btnEnviar: Button= findViewById(R.id.btnEnviar)

        btnEnviar.setOnClickListener {
            val mensaje = ctMensaje.text.toString()
            if(mensaje.isNotEmpty()){
                enviarMenajeAlReloj(mensaje)
            }
            else{
                Toast.makeText(this,"Escribe algo primero", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun enviarMenajeAlReloj(mensaje: String){
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for(node in nodes){
                Wearable.getMessageClient(this).sendMessage(
                    node.id,
                    "/mensaje_path",
                    mensaje.toByteArray()
                ).addOnSuccessListener {
                    Toast.makeText(this,"Enviado con exito", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error al enviar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume(){
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getMessageClient(this).removeListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if(messageEvent.path=="/respuesta_path"){
            val mensajeRecibido = String(messageEvent.data)
            runOnUiThread {
                ctMensaje.setText(mensajeRecibido)
            }
        }
    }
}