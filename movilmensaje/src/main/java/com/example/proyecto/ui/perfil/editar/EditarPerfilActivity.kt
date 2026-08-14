package com.example.proyecto.ui.perfil.editar
import com.example.movilmensaje.R

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyecto.data.repository.PerfilRepository
import com.example.proyecto.utils.TokenManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

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

        val tokenManager =
            TokenManager(this)

        val tvAvatar =
            findViewById<TextView>(R.id.tvEditarAvatar)

        val tvNombreCuenta =
            findViewById<TextView>(R.id.tvEditarNombreCuenta)

        val tvCorreoCuenta =
            findViewById<TextView>(R.id.tvEditarCorreoCuenta)

        val tvRolCuenta =
            findViewById<TextView>(R.id.tvEditarRolCuenta)

        val etPeso =
            findViewById<TextInputEditText>(
                R.id.etEditarPesoPerfil
            )

        val etAltura =
            findViewById<TextInputEditText>(
                R.id.etEditarAlturaPerfil
            )

        val etObjetivo =
            findViewById<TextInputEditText>(
                R.id.etEditarObjetivoPerfil
            )

        lifecycleScope.launch {

            val perfil = repository.obtenerPerfil()

            tvAvatar.text =
                perfil.nombre.firstOrNull()
                    ?.uppercase()
                    ?: "U"

            tvNombreCuenta.text =
                perfil.nombre.ifBlank { "Usuario FitTrack" }

            tvCorreoCuenta.text =
                perfil.email.ifBlank { "Sin correo registrado" }

            tvRolCuenta.text = perfil.rol

            etPeso.setText(perfil.peso.toString())
            etAltura.setText(perfil.altura.toString())
            etObjetivo.setText(perfil.objetivo)
        }

        findViewById<MaterialButton>(
            R.id.btnGuardarPerfil
        ).setOnClickListener {

            val peso =
                etPeso.text.toString().toFloatOrNull()

            val altura =
                etAltura.text.toString().toFloatOrNull()

            val objetivo =
                etObjetivo.text.toString().trim()

            if (peso == null || peso <= 0) {
                etPeso.error = "Ingresa un peso válido"
                return@setOnClickListener
            }

            if (altura == null || altura <= 0) {
                etAltura.error = "Ingresa una altura válida"
                return@setOnClickListener
            }

            repository.guardarDatosFisicos(
                peso = peso,
                altura = altura,
                objetivo = objetivo
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
