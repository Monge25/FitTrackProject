package com.example.proyecto.ui.progreso.detalle
import com.example.movilmensaje.R

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.data.repository.ProgresoRepository
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetalleSesionActivity :
    AppCompatActivity() {

    private lateinit var tvDetalleNombreSesion:
            TextView

    private lateinit var tvDetalleFechaSesion:
            TextView

    private lateinit var tvDetalleTiempoSesion:
            TextView

    private lateinit var tvDetalleEjerciciosSesion:
            TextView

    private lateinit var tvDetalleSeriesSesion:
            TextView

    private lateinit var tvDetalleFrecuenciaSesion:
            TextView

    private lateinit var btnCerrarDetalleSesion:
            MaterialButton

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_detalle_sesion
        )

        enlazarVistas()

        cargarSesion()

        btnCerrarDetalleSesion
            .setOnClickListener {
                finish()
            }
    }

    private fun enlazarVistas() {
        tvDetalleNombreSesion =
            findViewById(
                R.id.tvDetalleNombreSesion
            )

        tvDetalleFechaSesion =
            findViewById(
                R.id.tvDetalleFechaSesion
            )

        tvDetalleTiempoSesion =
            findViewById(
                R.id.tvDetalleTiempoSesion
            )

        tvDetalleEjerciciosSesion =
            findViewById(
                R.id.tvDetalleEjerciciosSesion
            )

        tvDetalleSeriesSesion =
            findViewById(
                R.id.tvDetalleSeriesSesion
            )

        tvDetalleFrecuenciaSesion =
            findViewById(
                R.id.tvDetalleFrecuenciaSesion
            )

        btnCerrarDetalleSesion =
            findViewById(
                R.id.btnCerrarDetalleSesion
            )
    }

    private fun cargarSesion() {
        val sesionId =
            intent.getLongExtra(
                EXTRA_SESION_ID,
                -1L
            )

        val repository =
            ProgresoRepository(this)

        val sesion =
            repository.obtenerSesion(
                sesionId
            )

        if (sesion == null) {
            finish()
            return
        }

        tvDetalleNombreSesion.text =
            sesion.nombreRutina

        tvDetalleFechaSesion.text =
            formatearFecha(
                sesion.fechaFin
            )

        tvDetalleTiempoSesion.text =
            formatearTiempo(
                sesion.duracionSegundos
            )

        tvDetalleEjerciciosSesion.text =
            sesion.ejerciciosCompletados
                .toString()

        tvDetalleSeriesSesion.text =
            sesion.seriesCompletadas
                .toString()

        tvDetalleFrecuenciaSesion.text =
            "${sesion.frecuenciaPromedio} BPM"
    }

    private fun formatearFecha(
        tiempo: Long
    ): String {
        val formato =
            SimpleDateFormat(
                "dd MMMM yyyy, HH:mm",
                Locale.getDefault()
            )

        return formato.format(
            Date(tiempo)
        )
    }

    private fun formatearTiempo(
        segundos: Int
    ): String {
        val horas =
            segundos / 3600

        val minutos =
            (segundos % 3600) / 60

        val segundosRestantes =
            segundos % 60

        return String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d",
            horas,
            minutos,
            segundosRestantes
        )
    }

    companion object {
        const val EXTRA_SESION_ID =
            "EXTRA_SESION_ID"
    }
}