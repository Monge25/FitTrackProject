package com.example.proyecto.ui.entrenamiento
import com.example.movilmensaje.R

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.Principal
import com.example.proyecto.data.repository.ProgresoRepository
import com.google.android.material.button.MaterialButton
import java.util.Locale

// Pantalla que se muestra justo al terminar un entrenamiento (llamada
// desde EntrenamientoActivoActivity). Reutiliza el mismo diseño que
// DetalleSesionActivity ("Resumen de la sesión"), pero el botón
// principal regresa al dashboard en vez de volver a Progreso.
class ResumenEntrenamientoActivity : AppCompatActivity() {

    private lateinit var tvResumenNombreRutina: TextView
    private lateinit var tvResumenTiempo: TextView
    private lateinit var tvResumenCalorias: TextView
    private lateinit var tvResumenFrecuencia: TextView
    private lateinit var tvResumenEjercicios: TextView
    private lateinit var tvResumenSeries: TextView
    private lateinit var btnResumenAceptar: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_resumen_entrenamiento)

        enlazarVistas()

        cargarSesion()

        btnResumenAceptar.setOnClickListener {
            irAlDashboard()
        }
    }

    private fun enlazarVistas() {
        tvResumenNombreRutina = findViewById(R.id.tvResumenNombreRutina)
        tvResumenTiempo = findViewById(R.id.tvResumenTiempo)
        tvResumenCalorias = findViewById(R.id.tvResumenCalorias)
        tvResumenFrecuencia = findViewById(R.id.tvResumenFrecuencia)
        tvResumenEjercicios = findViewById(R.id.tvResumenEjercicios)
        tvResumenSeries = findViewById(R.id.tvResumenSeries)
        btnResumenAceptar = findViewById(R.id.btnResumenAceptar)
    }

    private fun cargarSesion() {
        val sesionId = intent.getLongExtra(EXTRA_SESION_ID, -1L)

        val sesion = ProgresoRepository(this)
            .obtenerSesion(sesionId)

        if (sesion == null) {
            // No debería pasar (siempre se lanza justo tras guardar
            // la sesión), pero por seguridad evitamos un crash.
            irAlDashboard()
            return
        }

        tvResumenNombreRutina.text = sesion.nombreRutina

        tvResumenTiempo.text = formatearTiempo(sesion.duracionSegundos)

        // No se guarda un valor de calorías por sesión todavía; se
        // estima con la misma fórmula que usa el reloj (segundos/8)
        // para mantener consistencia entre ambos dispositivos.
        tvResumenCalorias.text = (sesion.duracionSegundos / 8).toString()

        tvResumenFrecuencia.text = sesion.frecuenciaPromedio.toString()

        tvResumenEjercicios.text = sesion.ejerciciosCompletados.toString()

        tvResumenSeries.text = sesion.seriesCompletadas.toString()
    }

    private fun formatearTiempo(segundos: Int): String {
        val horas = segundos / 3600
        val minutos = (segundos % 3600) / 60
        val segundosRestantes = segundos % 60

        return String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d",
            horas,
            minutos,
            segundosRestantes
        )
    }

    private fun irAlDashboard() {
        val intent = Intent(this, Principal::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_SESION_ID = "EXTRA_SESION_ID"
    }
}