package com.example.proyecto.ui.usuarios.nuevo
import com.example.movilmensaje.R

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class NuevoUsuarioActivity : AppCompatActivity() {

    private lateinit var etNombreUsuario:
            TextInputEditText

    private lateinit var etCorreoUsuario:
            TextInputEditText

    private lateinit var etPasswordUsuario:
            TextInputEditText

    private lateinit var etConfirmarPassword:
            TextInputEditText

    private lateinit var actvRolUsuario:
            AutoCompleteTextView

    private lateinit var btnGuardarUsuario:
            MaterialButton

    private lateinit var btnCancelarUsuario:
            MaterialButton

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_nuevo_usuario
        )

        inicializarComponentes()

        configurarSelectorRol()

        configurarEventos()
    }

    private fun inicializarComponentes() {

        etNombreUsuario = findViewById(
            R.id.etNombreUsuario
        )

        etCorreoUsuario = findViewById(
            R.id.etCorreoUsuario
        )

        etPasswordUsuario = findViewById(
            R.id.etPasswordUsuario
        )

        etConfirmarPassword = findViewById(
            R.id.etConfirmarPassword
        )

        actvRolUsuario = findViewById(
            R.id.actvRolUsuario
        )

        btnGuardarUsuario = findViewById(
            R.id.btnGuardarUsuario
        )

        btnCancelarUsuario = findViewById(
            R.id.btnCancelarUsuario
        )
    }

    private fun configurarSelectorRol() {

        val roles = listOf(
            "ADMINISTRADOR",
            "OPERADOR",
            "CLIENTE"
        )

        val adapterRoles = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            roles
        )

        actvRolUsuario.setAdapter(
            adapterRoles
        )

        actvRolUsuario.setOnClickListener {

            actvRolUsuario.showDropDown()
        }
    }

    private fun configurarEventos() {

        btnGuardarUsuario.setOnClickListener {

            guardarUsuario()
        }

        btnCancelarUsuario.setOnClickListener {

            finish()
        }
    }

    private fun guardarUsuario() {

        limpiarErrores()

        val nombre = etNombreUsuario.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val correo = etCorreoUsuario.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val password = etPasswordUsuario.text
            ?.toString()
            .orEmpty()

        val confirmarPassword =
            etConfirmarPassword.text
                ?.toString()
                .orEmpty()

        val rol = actvRolUsuario.text
            ?.toString()
            ?.trim()
            .orEmpty()

        if (nombre.isBlank()) {

            etNombreUsuario.error =
                "Ingresa el nombre del usuario"

            etNombreUsuario.requestFocus()

            return
        }

        if (correo.isBlank()) {

            etCorreoUsuario.error =
                "Ingresa el correo electrónico"

            etCorreoUsuario.requestFocus()

            return
        }

        if (
            !Patterns.EMAIL_ADDRESS
                .matcher(correo)
                .matches()
        ) {

            etCorreoUsuario.error =
                "Ingresa un correo válido"

            etCorreoUsuario.requestFocus()

            return
        }

        if (password.isBlank()) {

            etPasswordUsuario.error =
                "Ingresa una contraseña"

            etPasswordUsuario.requestFocus()

            return
        }

        if (password.length < 6) {

            etPasswordUsuario.error =
                "La contraseña debe tener mínimo 6 caracteres"

            etPasswordUsuario.requestFocus()

            return
        }

        if (confirmarPassword.isBlank()) {

            etConfirmarPassword.error =
                "Confirma la contraseña"

            etConfirmarPassword.requestFocus()

            return
        }

        if (password != confirmarPassword) {

            etConfirmarPassword.error =
                "Las contraseñas no coinciden"

            etConfirmarPassword.requestFocus()

            return
        }

        if (
            rol != "ADMINISTRADOR" &&
            rol != "OPERADOR"
        ) {

            actvRolUsuario.error =
                "Selecciona un rol válido"

            actvRolUsuario.requestFocus()

            return
        }

        devolverUsuario(
            nombre = nombre,
            correo = correo,
            password = password,
            rol = rol
        )
    }

    private fun devolverUsuario(nombre: String, correo: String, password: String, rol: String) {
        val resultado = Intent().apply {
            putExtra(EXTRA_NOMBRE,   nombre)
            putExtra(EXTRA_CORREO,   correo)
            putExtra(EXTRA_PASSWORD, password)
            putExtra(EXTRA_ROL,      rol)
        }
        setResult(RESULT_OK, resultado)
        finish()
    }

    private fun limpiarErrores() {

        etNombreUsuario.error =
            null

        etCorreoUsuario.error =
            null

        etPasswordUsuario.error =
            null

        etConfirmarPassword.error =
            null

        actvRolUsuario.error =
            null
    }

    companion object {

        const val EXTRA_NOMBRE =
            "EXTRA_NOMBRE"

        const val EXTRA_CORREO =
            "EXTRA_CORREO"

        const val EXTRA_ROL =
            "EXTRA_ROL"

        const val EXTRA_PASSWORD =
            "EXTRA_PASSWORD"
    }
}