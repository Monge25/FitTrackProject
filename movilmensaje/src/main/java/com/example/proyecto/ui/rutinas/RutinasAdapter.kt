package com.example.proyecto.ui.rutinas
import com.example.movilmensaje.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.Rutina

class RutinasAdapter(
    private var rutinas: List<Rutina>,
    private val onRutinaClick: (Rutina) -> Unit
) : RecyclerView.Adapter<RutinasAdapter.RutinaViewHolder>() {

    inner class RutinaViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvNombre: TextView =
            itemView.findViewById(
                R.id.tvNombreRutina
            )

        private val tvDescripcion: TextView =
            itemView.findViewById(
                R.id.tvDescripcionRutina
            )

        private val tvDuracion: TextView =
            itemView.findViewById(
                R.id.tvDuracionRutina
            )

        private val tvEjercicios: TextView =
            itemView.findViewById(
                R.id.tvEjerciciosRutina
            )


        fun bind(
            rutina: Rutina
        ) {

            tvNombre.text =
                rutina.nombre

            tvDescripcion.text =
                rutina.descripcion

            tvDuracion.text =
                "${rutina.duracion} min"

            tvEjercicios.text =
                "${rutina.ejercicios} ejercicios"

            itemView.setOnClickListener {

                onRutinaClick(
                    rutina
                )
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RutinaViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_rutina,
                parent,
                false
            )

        return RutinaViewHolder(
            view
        )
    }


    override fun onBindViewHolder(
        holder: RutinaViewHolder,
        position: Int
    ) {

        holder.bind(
            rutinas[position]
        )
    }


    override fun getItemCount(): Int {

        return rutinas.size
    }


    fun actualizarLista(
        nuevaLista: List<Rutina>
    ) {

        rutinas = nuevaLista

        notifyDataSetChanged()
    }
}