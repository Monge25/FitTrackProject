package com.example.proyecto.ui.rutinas.detalle
import com.example.movilmensaje.R

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class DetalleRutinaActivity : AppCompatActivity() {

    private lateinit var btnEmpezarEntrenamiento:
            MaterialButton
    private lateinit var tvNombreRutina: TextView

    private lateinit var tvDescripcionRutina: TextView

    private lateinit var tvNivelRutina: TextView

    private lateinit var tvDuracionRutina: TextView

    private lateinit var tvEjerciciosRutina: TextView

    private lateinit var btnVolver: MaterialButton


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_detalle_rutina
        )

        inicializarComponentes(
        )

        cargarInformacionRutina()

        configurarEventos()
    }

    private fun iniciarEntrenamiento() {

        val nombreRutina =
            intent.getStringExtra(
                "RUTINA_NOMBRE"
            ) ?: "Entrenamiento"

        val intentEntrenamiento =
            android.content.Intent(
                this,
                com.example.proyecto.ui.entrenamiento
                    .EntrenamientoActivoActivity::class.java
            )

        intentEntrenamiento.putExtra(
            "RUTINA_NOMBRE",
            nombreRutina
        )

        startActivity(
            intentEntrenamiento
        )
    }

    private fun inicializarComponentes() {

        tvNombreRutina =
            findViewById(
                R.id.tvDetalleNombreRutina
            )

        tvDescripcionRutina =
            findViewById(
                R.id.tvDetalleDescripcionRutina
            )

        tvNivelRutina =
            findViewById(
                R.id.tvDetalleNivelRutina
            )

        tvDuracionRutina =
            findViewById(
                R.id.tvDetalleDuracionRutina
            )

        tvEjerciciosRutina =
            findViewById(
                R.id.tvDetalleEjerciciosRutina
            )

        btnVolver =
            findViewById(
                R.id.btnVolverRutinas
            )

        btnEmpezarEntrenamiento =
            findViewById(
                R.id.btnEmpezarEntrenamiento
            )
    }


    private fun cargarInformacionRutina() {

        val nombre =
            intent.getStringExtra(
                "RUTINA_NOMBRE"
            ) ?: "Rutina"

        val descripcion =
            intent.getStringExtra(
                "RUTINA_DESCRIPCION"
            ) ?: ""

        val nivel =
            intent.getStringExtra(
                "RUTINA_NIVEL"
            ) ?: "Sin nivel"

        val duracion =
            intent.getIntExtra(
                "RUTINA_DURACION",
                0
            )

        val ejercicios =
            intent.getIntExtra(
                "RUTINA_EJERCICIOS",
                0
            )

        tvNombreRutina.text =
            nombre.uppercase()

        tvDescripcionRutina.text =
            descripcion

        tvNivelRutina.text =
            nivel

        tvDuracionRutina.text =
            "$duracion min"

        tvEjerciciosRutina.text =
            "$ejercicios ejercicios"
    }


    private fun configurarEventos() {

        btnVolver.setOnClickListener {

            finish()
        }

        btnEmpezarEntrenamiento.setOnClickListener {

            iniciarEntrenamiento()
        }

        btnVolver.setOnClickListener {

            finish()
        }
    }
}