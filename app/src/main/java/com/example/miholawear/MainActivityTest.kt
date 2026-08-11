package com.example.miholawear

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import org.w3c.dom.Text

class MainActivityTest : AppCompatActivity(), MessageClient.OnMessageReceivedListener {
    lateinit var ctMensaje: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_test)
        val btnEnviar: Button = findViewById(R.id.btnEnviar)
        ctMensaje = findViewById(R.id.ctMensaje)


        btnEnviar.setOnClickListener {
            val mensaje = ctMensaje.text.toString()
            if(mensaje.isNotEmpty()){
                enviarMenajeAlMovil(mensaje)
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
        if(messageEvent.path=="/mensaje_path"){
            val mensajeRecibido = String(messageEvent.data)
            runOnUiThread {
                ctMensaje.text = mensajeRecibido
            }
        }
    }

    private fun enviarMenajeAlMovil(mensaje: String){
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for(node in nodes){
                Wearable.getMessageClient(this).sendMessage(
                    node.id,
                    "/respuesta_path",
                    mensaje.toByteArray()
                ).addOnSuccessListener {
                    Toast.makeText(this,"Enviado con exito", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error al enviar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}