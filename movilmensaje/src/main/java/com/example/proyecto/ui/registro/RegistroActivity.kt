package com.example.proyecto.ui.registro
import com.example.movilmensaje.R

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.data.mock.UsuariosCatalog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

/**
 * Registro de una cuenta nueva.
 *
 * IMPORTANTE: esto agrega la cuenta a UsuariosCatalog (el catálogo
 * local que alimenta la pantalla "Usuarios"). NO crea una cuenta
 * con la que se pueda iniciar sesión de verdad, porque el login
 * (MainActivity -> AuthRepository -> ClientesApiService.login) pega
 * contra el backend real, que todavía no tiene un endpoint de
 * registro.
 *
 * TODO: cuando exista POST /api/auth/registro en el backend,
 * reemplazar la llamada a UsuariosCatalog.registrarUsuario(...) por
 * una llamada real, similar a como AuthRepository.login() usa
 * RetrofitInstance.api.login(...).
 */
class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmarPassword: TextInputEditText

    private lateinit var btnRegistrar: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_registro
        )

        inicializarComponentes()

        configurarEventos()
    }

    private fun inicializarComponentes() {

        etNombre = findViewById(
            R.id.etNombreRegistro
        )

        etCorreo = findViewById(
            R.id.etCorreoRegistro
        )

        etPassword = findViewById(
            R.id.etPasswordRegistro
        )

        etConfirmarPassword = findViewById(
            R.id.etConfirmarPasswordRegistro
        )

        btnRegistrar = findViewById(
            R.id.btnRegistrar
        )

        findViewById<android.widget.TextView>(
            R.id.tvVolverALogin
        ).setOnClickListener {
            finish()
        }
    }

    private fun configurarEventos() {

        btnRegistrar.setOnClickListener {
            registrar()
        }
    }

    private fun registrar() {

        limpiarErrores()

        val nombre = etNombre.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val correo = etCorreo.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val password = etPassword.text
            ?.toString()
            .orEmpty()

        val confirmarPassword =
            etConfirmarPassword.text
                ?.toString()
                .orEmpty()


        if (nombre.isBlank()) {

            etNombre.error =
                "Ingresa tu nombre"

            etNombre.requestFocus()

            return
        }


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


        if (UsuariosCatalog.existeCorreo(correo)) {

            etCorreo.error =
                "Ya existe una cuenta con este correo"

            etCorreo.requestFocus()

            return
        }


        if (password.isBlank()) {

            etPassword.error =
                "Ingresa una contraseña"

            etPassword.requestFocus()

            return
        }


        if (password.length < 6) {

            etPassword.error =
                "La contraseña debe tener mínimo 6 caracteres"

            etPassword.requestFocus()

            return
        }


        if (confirmarPassword.isBlank()) {

            etConfirmarPassword.error =
                "Confirma tu contraseña"

            etConfirmarPassword.requestFocus()

            return
        }


        if (password != confirmarPassword) {

            etConfirmarPassword.error =
                "Las contraseñas no coinciden"

            etConfirmarPassword.requestFocus()

            return
        }


        UsuariosCatalog.registrarUsuario(
            nombre = nombre,
            correo = correo
        )

        Toast.makeText(
            this,
            "Cuenta creada. Un administrador debe " +
                    "activarte el acceso para poder " +
                    "iniciar sesión.",
            Toast.LENGTH_LONG
        ).show()

        finish()
    }

    private fun limpiarErrores() {

        etNombre.error = null
        etCorreo.error = null
        etPassword.error = null
        etConfirmarPassword.error = null
    }
}