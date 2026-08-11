package com.example.proyecto.ui.perfil.editar
import com.example.movilmensaje.R

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.data.repository.PerfilRepository
import com.google.android.material.button.MaterialButton

class EditarPerfilActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_editar_perfil
        )

        val repository =
            PerfilRepository(this)

        val perfil =
            repository.obtenerPerfil()

        val etNombre =
            findViewById<EditText>(
                R.id.etEditarNombrePerfil
            )

        val etPeso =
            findViewById<EditText>(
                R.id.etEditarPesoPerfil
            )

        val etAltura =
            findViewById<EditText>(
                R.id.etEditarAlturaPerfil
            )

        val etObjetivo =
            findViewById<EditText>(
                R.id.etEditarObjetivoPerfil
            )

        etNombre.setText(perfil.nombre)
        etPeso.setText(perfil.peso.toString())
        etAltura.setText(perfil.altura.toString())
        etObjetivo.setText(perfil.objetivo)

        findViewById<MaterialButton>(
            R.id.btnGuardarPerfil
        ).setOnClickListener {
            val nombre =
                etNombre.text.toString().trim()

            val peso =
                etPeso.text.toString().toFloatOrNull()

            val altura =
                etAltura.text.toString().toFloatOrNull()

            val objetivo =
                etObjetivo.text.toString().trim()

            if (nombre.isBlank()) {
                etNombre.error = "Ingresa tu nombre"
                return@setOnClickListener
            }

            if (peso == null || peso <= 0) {
                etPeso.error = "Ingresa un peso válido"
                return@setOnClickListener
            }

            if (altura == null || altura <= 0) {
                etAltura.error = "Ingresa una altura válida"
                return@setOnClickListener
            }

            repository.guardarPerfil(
                perfil.copy(
                    nombre = nombre,
                    peso = peso,
                    altura = altura,
                    objetivo = objetivo
                )
            )

            Toast.makeText(
                this,
                "Perfil actualizado",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        findViewById<MaterialButton>(
            R.id.btnCancelarPerfil
        ).setOnClickListener {
            finish()
        }
    }
}