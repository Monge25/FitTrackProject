package com.example.proyecto.ui.rutinas
import com.example.movilmensaje.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.Rutina
import com.google.android.material.switchmaterial.SwitchMaterial

class RutinasAdapter(
    private var rutinas: List<Rutina>,
    private val onRutinaClick: (Rutina) -> Unit,
    private val onToggleActivo: (Rutina, Boolean) -> Unit
) : RecyclerView.Adapter<RutinasAdapter.RutinaViewHolder>() {

    inner class RutinaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreRutina)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionRutina)
        private val tvDuracion: TextView = itemView.findViewById(R.id.tvDuracionRutina)
        private val tvEjercicios: TextView = itemView.findViewById(R.id.tvEjerciciosRutina)
        private val switchActiva: SwitchMaterial = itemView.findViewById(R.id.switchRutinaActiva)

        fun bind(rutina: Rutina) {
            tvNombre.text = rutina.nombre
            tvDescripcion.text = objetivoTexto(rutina.objetivo)
            tvDuracion.text = nivelTexto(rutina.nivel)
            tvEjercicios.text = "${rutina.ejercicios.size} ejercicios"

            itemView.alpha = if (rutina.esActivo) 1f else 0.55f

            itemView.setOnClickListener {
                onRutinaClick(rutina)
            }

            // Se quita el listener antes de fijar el estado para que
            // no se dispare solo al reciclar la vista (el "toggle"
            // real es lo que hace el usuario tocándolo).
            switchActiva.setOnCheckedChangeListener(null)
            switchActiva.isChecked = rutina.esActivo

            switchActiva.setOnCheckedChangeListener { _, activo ->
                if (activo != rutina.esActivo) {
                    onToggleActivo(rutina, activo)
                }
            }
        }

        private fun nivelTexto(nivel: Int) = when (nivel) {
            0 -> "Principiante"
            1 -> "Intermedio"
            2 -> "Avanzado"
            else -> "Desconocido"
        }

        private fun objetivoTexto(objetivo: Int) = when (objetivo) {
            0 -> "Hipertrofia"
            1 -> "Fuerza"
            2 -> "Resistencia"
            3 -> "Cardio"
            4 -> "Movilidad"
            else -> "Sin objetivo"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RutinaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rutina, parent, false)
        return RutinaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RutinaViewHolder, position: Int) {
        holder.bind(rutinas[position])
    }

    override fun getItemCount(): Int = rutinas.size

    fun actualizarLista(nuevaLista: List<Rutina>) {
        rutinas = nuevaLista
        notifyDataSetChanged()
    }
}