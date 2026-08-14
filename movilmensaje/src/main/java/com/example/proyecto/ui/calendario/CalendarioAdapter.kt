package com.example.proyecto.ui.calendario
import com.example.movilmensaje.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.EntrenamientoProgramado

class CalendarioAdapter(
    private var entrenamientos: List<EntrenamientoProgramado>,
    private val onEntrenamientoClick:
        (EntrenamientoProgramado) -> Unit
) : RecyclerView.Adapter<CalendarioAdapter.CalendarioViewHolder>() {

    inner class CalendarioViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvDia: TextView =
            itemView.findViewById(R.id.tvDiaCalendario)

        private val tvNombre: TextView =
            itemView.findViewById(
                R.id.tvNombreEntrenamientoCalendario
            )

        private val tvHorario: TextView =
            itemView.findViewById(
                R.id.tvHorarioEntrenamientoCalendario
            )

        private val tvDuracion: TextView =
            itemView.findViewById(
                R.id.tvDuracionEntrenamientoCalendario
            )

        private val tvEstado: TextView =
            itemView.findViewById(
                R.id.tvEstadoEntrenamientoCalendario
            )

        fun bind(
            entrenamiento: EntrenamientoProgramado
        ) {
            tvDia.text =
                obtenerAbreviatura(entrenamiento.fecha)

            tvNombre.text =
                entrenamiento.nombreRutina

            tvHorario.text =
                "${entrenamiento.fecha} · ${entrenamiento.hora}"

            tvDuracion.text =
                "${entrenamiento.duracionMinutos} minutos"

            tvEstado.text =
                when (entrenamiento.calcularEstado()) {
                    EstadoEntrenamiento.CUMPLIDA -> "CUMPLIDA"
                    EstadoEntrenamiento.VENCIDA -> "VENCIDA"
                    EstadoEntrenamiento.PROXIMA -> "PRÓXIMA"
                }

            tvEstado.setTextColor(
                itemView.context.getColor(
                    when (entrenamiento.calcularEstado()) {
                        EstadoEntrenamiento.CUMPLIDA -> android.R.color.holo_green_dark
                        EstadoEntrenamiento.VENCIDA -> android.R.color.holo_red_light
                        EstadoEntrenamiento.PROXIMA -> android.R.color.holo_orange_dark
                    }
                )
            )

            itemView.setOnClickListener {
                onEntrenamientoClick(entrenamiento)
            }
        }

        private fun obtenerAbreviatura(
            dia: String
        ): String {
            return when (dia.lowercase()) {
                "lunes" -> "LU"
                "martes" -> "MA"
                "miércoles", "miercoles" -> "MI"
                "jueves" -> "JU"
                "viernes" -> "VI"
                "sábado", "sabado" -> "SA"
                "domingo" -> "DO"
                else -> dia.take(2).uppercase()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CalendarioViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_entrenamiento_programado,
                parent,
                false
            )

        return CalendarioViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CalendarioViewHolder,
        position: Int
    ) {
        holder.bind(entrenamientos[position])
    }

    override fun getItemCount(): Int =
        entrenamientos.size

    fun actualizarLista(
        nuevaLista: List<EntrenamientoProgramado>
    ) {
        entrenamientos = nuevaLista
        notifyDataSetChanged()
    }
}