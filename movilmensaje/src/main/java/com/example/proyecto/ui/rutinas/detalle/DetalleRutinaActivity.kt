package com.example.proyecto.ui.rutinas.detalle
import com.example.movilmensaje.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.EjercicioApi
import com.example.proyecto.data.repository.RutinasRepository
import com.example.proyecto.utils.TokenManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class DetalleRutinaActivity : AppCompatActivity() {

    private lateinit var rvEjercicios: RecyclerView
    private lateinit var btnEmpezarEntrenamiento: MaterialButton
    private lateinit var tvNombreRutina: TextView
    private lateinit var tvDescripcionRutina: TextView
    private lateinit var tvNivelRutina: TextView
    private lateinit var tvDuracionRutina: TextView
    private lateinit var tvEjerciciosRutina: TextView
    private lateinit var btnVolver: MaterialButton

    private lateinit var progressDetalle: android.widget.ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_rutina)
        inicializarComponentes()
        cargarInformacionRutina()
        configurarEventos()
    }

    private fun inicializarComponentes() {
        rvEjercicios        = findViewById(R.id.rvDetalleEjercicios)
        tvNombreRutina      = findViewById(R.id.tvDetalleNombreRutina)
        tvDescripcionRutina = findViewById(R.id.tvDetalleDescripcionRutina)
        tvNivelRutina       = findViewById(R.id.tvDetalleNivelRutina)
        tvDuracionRutina    = findViewById(R.id.tvDetalleDuracionRutina)
        tvEjerciciosRutina  = findViewById(R.id.tvDetalleEjerciciosRutina)
        btnVolver               = findViewById(R.id.btnVolverRutinas)
        btnEmpezarEntrenamiento = findViewById(R.id.btnEmpezarEntrenamiento)

        rvEjercicios.layoutManager = LinearLayoutManager(this)
        progressDetalle = findViewById(R.id.progressDetalleRutina)
    }

    private fun cargarInformacionRutina() {
        val nombre   = intent.getStringExtra("RUTINA_NOMBRE") ?: "Rutina"
        val rutinaId = intent.getIntExtra("RUTINA_ID", -1)

        tvNombreRutina.text      = nombre.uppercase()
        tvDescripcionRutina.text = ""
        tvNivelRutina.text       = ""
        tvDuracionRutina.text    = "0 min"
        tvEjerciciosRutina.text  = "0 ejercicios"

        if (rutinaId == -1) return

        lifecycleScope.launch {
            progressDetalle.visibility = View.VISIBLE
            val token = TokenManager(this@DetalleRutinaActivity).obtenerBearer()
            RutinasRepository().obtenerPorId(token, rutinaId).fold(
                onSuccess = { rutina ->
                    progressDetalle.visibility = View.GONE
                    val nivelTexto = when (rutina.nivel) {
                        0    -> "Principiante"
                        1    -> "Intermedio"
                        else -> "Avanzado"
                    }
                    val objetivoTexto = when (rutina.objetivo) {
                        0    -> "Hipertrofia"
                        1    -> "Fuerza"
                        2    -> "Resistencia"
                        3    -> "Cardio"
                        else -> "Movilidad"
                    }
                    val duracionEstimada = rutina.ejercicios.sumOf { ej ->
                        val tiempoActivo = ej.series * ej.repeticiones * 4
                        val descansos    = (ej.series - 1).coerceAtLeast(0) * ej.descanso
                        tiempoActivo + descansos
                    } / 60

                    tvNivelRutina.text       = nivelTexto
                    tvDescripcionRutina.text = objetivoTexto
                    tvDuracionRutina.text    = "${duracionEstimada.coerceAtLeast(1)} min"
                    tvEjerciciosRutina.text  = "${rutina.ejercicios.size} ejercicios"

                    android.util.Log.d("DETALLE", "Ejercicios recibidos: ${rutina.ejercicios.size}")
                    runOnUiThread {
                        cargarListaEjercicios(rutina.ejercicios)
                    }
                },
                onFailure = {
                    progressDetalle.visibility = View.GONE
                    tvDuracionRutina.text   = "-- min"
                    tvEjerciciosRutina.text = "-- ejercicios"
                }
            )
        }
    }

    private fun cargarListaEjercicios(ejercicios: List<EjercicioApi>) {
        rvEjercicios.adapter = DetalleEjerciciosAdapter(ejercicios)
    }

    private fun iniciarEntrenamiento() {
        val nombreRutina = intent.getStringExtra("RUTINA_NOMBRE") ?: "Entrenamiento"
        val rutinaId     = intent.getIntExtra("RUTINA_ID", -1)
        val sesionId     = intent.getIntExtra("ENTRENAMIENTO_PROGRAMADO_ID", -1)

        val intent = android.content.Intent(
            this,
            com.example.proyecto.ui.entrenamiento.EntrenamientoActivoActivity::class.java
        )
        intent.putExtra("RUTINA_NOMBRE", nombreRutina)
        intent.putExtra("RUTINA_ID",     rutinaId)
        intent.putExtra("SESION_ID",     sesionId)
        startActivity(intent)
    }

    private fun configurarEventos() {
        btnVolver.setOnClickListener { finish() }
        btnEmpezarEntrenamiento.setOnClickListener { iniciarEntrenamiento() }
    }
}

// ViewHolder fuera de la clase para evitar el 'inner class' en object anónimo
//class DetalleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//    val tvTexto: TextView = view.findViewById(R.id.tvEjercicioDetalleTexto)
//}