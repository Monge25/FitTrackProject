package com.example.proyecto.ui.progreso
import com.example.movilmensaje.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.SesionEntrenamiento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistorialAdapter(
    private var sesiones:
    List<SesionEntrenamiento>,
    private val onSesionClick:
        (SesionEntrenamiento) -> Unit
) : RecyclerView.Adapter<
        HistorialAdapter.SesionViewHolder
        >() {

    inner class SesionViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvNombre: TextView =
            itemView.findViewById(
                R.id.tvHistorialNombre
            )

        private val tvFecha: TextView =
            itemView.findViewById(
                R.id.tvHistorialFecha
            )

        private val tvResumen: TextView =
            itemView.findViewById(
                R.id.tvHistorialResumen
            )

        fun bind(
            sesion: SesionEntrenamiento
        ) {
            tvNombre.text =
                sesion.nombreRutina

            tvFecha.text =
                formatearFecha(
                    sesion.fechaFin
                )

            tvResumen.text =
                "${formatearDuracion(sesion.duracionSegundos)} • " +
                        "${sesion.ejerciciosCompletados} ejercicios"

            itemView.setOnClickListener {
                onSesionClick(sesion)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SesionViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_historial_entrenamiento,
                    parent,
                    false
                )

        return SesionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SesionViewHolder,
        position: Int
    ) {
        holder.bind(
            sesiones[position]
        )
    }

    override fun getItemCount(): Int {
        return sesiones.size
    }

    fun actualizarLista(
        nuevaLista:
        List<SesionEntrenamiento>
    ) {
        sesiones = nuevaLista

        notifyDataSetChanged()
    }

    private fun formatearFecha(
        tiempo: Long
    ): String {
        val formato =
            SimpleDateFormat(
                "dd MMM yyyy, HH:mm",
                Locale.getDefault()
            )

        return formato.format(
            Date(tiempo)
        )
    }

    private fun formatearDuracion(
        segundos: Int
    ): String {
        val horas =
            segundos / 3600

        val minutos =
            (segundos % 3600) / 60

        return if (horas > 0) {
            "${horas}h ${minutos}min"
        } else {
            "${minutos} min"
        }
    }
}