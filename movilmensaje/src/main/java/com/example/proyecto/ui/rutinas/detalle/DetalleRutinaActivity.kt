package com.example.proyecto.ui.rutinas.detalle
import com.example.movilmensaje.R

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.Rutina
import com.example.proyecto.data.repository.RutinasRepository
import com.example.proyecto.utils.TokenManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class DetalleRutinaActivity : AppCompatActivity() {

    private lateinit var tvNombreRutina: TextView
    private lateinit var tvDescripcionRutina: TextView
    private lateinit var tvNivelRutina: TextView
    private lateinit var tvDuracionRutina: TextView
    private lateinit var tvEjerciciosRutina: TextView

    private lateinit var progressDetalle: ProgressBar
    private lateinit var rvEjercicios: RecyclerView
    private lateinit var tvSinEjercicios: TextView

    private lateinit var btnEmpezarEntrenamiento: MaterialButton
    private lateinit var btnVolver: MaterialButton

    private lateinit var tokenManager: TokenManager
    private val rutinasRepository = RutinasRepository()

    private var rutinaId: Int = 0
    private var rutinaActual: Rutina? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detalle_rutina)

        tokenManager = TokenManager(this)

        inicializarComponentes()
        mostrarDatosDelIntent()
        configurarEventos()
        cargarRutinaDesdeApi()
    }

    private fun inicializarComponentes() {

        tvNombreRutina = findViewById(R.id.tvDetalleNombreRutina)
        tvDescripcionRutina = findViewById(R.id.tvDetalleDescripcionRutina)
        tvNivelRutina = findViewById(R.id.tvDetalleNivelRutina)
        tvDuracionRutina = findViewById(R.id.tvDetalleDuracionRutina)
        tvEjerciciosRutina = findViewById(R.id.tvDetalleEjerciciosRutina)

        progressDetalle = findViewById(R.id.progressDetalleRutina)
        rvEjercicios = findViewById(R.id.rvDetalleEjercicios)
        tvSinEjercicios = findViewById(R.id.tvDetalleSinEjercicios)

        btnEmpezarEntrenamiento = findViewById(R.id.btnEmpezarEntrenamiento)
        btnVolver = findViewById(R.id.btnVolverRutinas)

        rvEjercicios.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Pinta de inmediato lo que ya venía en el Intent (para que la
     * pantalla no se sienta vacía mientras carga), y luego
     * cargarRutinaDesdeApi() lo reemplaza con los datos reales y
     * definitivos de la rutina.
     */
    private fun mostrarDatosDelIntent() {

        rutinaId = intent.getIntExtra("RUTINA_ID", 0)

        val nombre = intent.getStringExtra("RUTINA_NOMBRE") ?: "Rutina"
        val descripcion = intent.getStringExtra("RUTINA_DESCRIPCION") ?: ""
        val nivel = intent.getStringExtra("RUTINA_NIVEL") ?: "Sin nivel"
        val duracion = intent.getIntExtra("RUTINA_DURACION", 0)
        val ejercicios = intent.getIntExtra("RUTINA_EJERCICIOS", 0)

        tvNombreRutina.text = nombre.uppercase()
        tvDescripcionRutina.text = descripcion
        tvNivelRutina.text = nivel
        tvDuracionRutina.text = "$duracion min"
        tvEjerciciosRutina.text = "$ejercicios ejercicios"
    }

    private fun cargarRutinaDesdeApi() {

        if (rutinaId == 0) {
            progressDetalle.visibility = View.GONE
            return
        }

        progressDetalle.visibility = View.VISIBLE

        lifecycleScope.launch {

            val token = tokenManager.obtenerBearer()

            rutinasRepository.obtenerPorId(token, rutinaId).fold(
                onSuccess = { rutina ->
                    rutinaActual = rutina
                    progressDetalle.visibility = View.GONE
                    mostrarRutina(rutina)
                },
                onFailure = {
                    progressDetalle.visibility = View.GONE
                    Toast.makeText(
                        this@DetalleRutinaActivity,
                        "No se pudo cargar la rutina completa",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun mostrarRutina(rutina: Rutina) {

        tvNombreRutina.text = rutina.nombre.uppercase()
        tvDescripcionRutina.text = objetivoTexto(rutina.objetivo)
        tvNivelRutina.text = nivelTexto(rutina.nivel)
        tvDuracionRutina.text = "${calcularDuracionEstimada(rutina)} min"
        tvEjerciciosRutina.text = "${rutina.ejercicios.size} ejercicios"

        if (rutina.ejercicios.isEmpty()) {
            rvEjercicios.visibility = View.GONE
            tvSinEjercicios.visibility = View.VISIBLE
        } else {
            rvEjercicios.visibility = View.VISIBLE
            tvSinEjercicios.visibility = View.GONE
            rvEjercicios.adapter = DetalleEjerciciosAdapter(rutina.ejercicios)
        }
    }

    private fun calcularDuracionEstimada(rutina: Rutina): Int {

        if (rutina.ejercicios.isEmpty()) return 0

        val segundosTotales = rutina.ejercicios.sumOf { ejercicio ->
            val tiempoActivo = ejercicio.series * ejercicio.repeticiones * 4
            val descansos = (ejercicio.series - 1).coerceAtLeast(0) * ejercicio.descanso
            tiempoActivo + descansos
        }

        val transiciones = (rutina.ejercicios.size - 1).coerceAtLeast(0) * 60

        return ((segundosTotales + transiciones) / 60f).toInt().coerceAtLeast(1)
    }

    private fun nivelTexto(nivel: Int) = when (nivel) {
        0 -> "Principiante"
        1 -> "Intermedio"
        else -> "Avanzado"
    }

    private fun objetivoTexto(objetivo: Int) = when (objetivo) {
        0 -> "Hipertrofia"
        1 -> "Fuerza"
        2 -> "Resistencia"
        3 -> "Cardio"
        else -> "Movilidad"
    }

    private fun configurarEventos() {

        btnVolver.setOnClickListener {
            finish()
        }

        btnEmpezarEntrenamiento.setOnClickListener {
            iniciarEntrenamiento()
        }
    }

    private fun iniciarEntrenamiento() {

        val nombreRutina = rutinaActual?.nombre
            ?: intent.getStringExtra("RUTINA_NOMBRE")
            ?: "Entrenamiento"

        val intentEntrenamiento = Intent(
            this,
            com.example.proyecto.ui.entrenamiento.EntrenamientoActivoActivity::class.java
        )

        intentEntrenamiento.putExtra("RUTINA_ID", rutinaId)
        intentEntrenamiento.putExtra("RUTINA_NOMBRE", nombreRutina)

        startActivity(intentEntrenamiento)
    }
}