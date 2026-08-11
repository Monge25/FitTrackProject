package com.example.proyecto.ui.recuperar
import com.example.movilmensaje.R

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

/**
 * Recuperación de contraseña.
 *
 * IMPORTANTE: no existe ningún backend de correo ni endpoint de
 * recuperación todavía (ClientesApiService solo tiene login y CRUD
 * de clientes). Esta pantalla valida el correo y muestra la
 * pantalla de confirmación que vería el usuario, pero NO envía
 * ningún correo real.
 *
 * TODO: cuando exista un endpoint como
 * POST /api/auth/recuperar-password, reemplazar
 * mostrarConfirmacion() por una llamada real (con su propio
 * Result<Unit> en un repositorio, igual que AuthRepository.login).
 * Por seguridad, ese endpoint normalmente responde igual exista o
 * no el correo, para no revelar qué correos están registrados —
 * por eso aquí tampoco se distingue.
 */
class RecuperarPasswordActivity : AppCompatActivity() {

    private lateinit var llPasoCorreo: View
    private lateinit var llPasoConfirmacion: View

    private lateinit var etCorreo: TextInputEditText
    private lateinit var btnEnviar: MaterialButton
    private lateinit var btnVolverAIntentar: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_recuperar_password
        )

        inicializarComponentes()

        configurarEventos()
    }

    private fun inicializarComponentes() {

        llPasoCorreo = findViewById(
            R.id.llPasoCorreo
        )

        llPasoConfirmacion = findViewById(
            R.id.llPasoConfirmacion
        )

        etCorreo = findViewById(
            R.id.etCorreoRecuperar
        )

        btnEnviar = findViewById(
            R.id.btnEnviarRecuperacion
        )

        btnVolverAIntentar = findViewById(
            R.id.btnVolverAIntentar
        )
    }

    private fun configurarEventos() {

        btnEnviar.setOnClickListener {
            enviarRecuperacion()
        }

        btnVolverAIntentar.setOnClickListener {
            mostrarPasoCorreo()
        }

        findViewById<android.widget.TextView>(
            R.id.tvVolverALoginRecuperar
        ).setOnClickListener {
            finish()
        }
    }

    private fun enviarRecuperacion() {

        etCorreo.error = null

        val correo = etCorreo.text
            ?.toString()
            ?.trim()
            .orEmpty()

        if (correo.isBlank()) {

            etCorreo.error =
                "Ingresa tu correo"

            etCorreo.requestFocus()

            return
        }

        if (
            !Patterns.EMAIL_ADDRESS
                .matcher(correo)
                .matches()
        ) {

            etCorreo.error =
                "Ingresa un correo válido"

            etCorreo.requestFocus()

            return
        }

        // TODO: aquí va la llamada real al backend cuando exista
        // el endpoint de recuperación de contraseña.

        mostrarConfirmacion(correo)
    }

    private fun mostrarConfirmacion(
        correo: String
    ) {

        val tvMensaje =
            findViewById<android.widget.TextView>(
                R.id.tvMensajeConfirmacion
            )

        tvMensaje.text =
            "Si $correo está registrado, te enviamos " +
                    "instrucciones para restablecer tu contraseña."

        llPasoCorreo.visibility = View.GONE
        llPasoConfirmacion.visibility = View.VISIBLE
    }

    private fun mostrarPasoCorreo() {

        etCorreo.setText("")

        llPasoConfirmacion.visibility = View.GONE
        llPasoCorreo.visibility = View.VISIBLE
    }
}