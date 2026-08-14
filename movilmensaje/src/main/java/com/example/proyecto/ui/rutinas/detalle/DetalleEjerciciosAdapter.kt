package com.example.proyecto.ui.rutinas.detalle
import com.example.movilmensaje.R

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.EjercicioApi

class DetalleEjerciciosAdapter(
    private val ejercicios: List<EjercicioApi>
) : RecyclerView.Adapter<DetalleEjerciciosAdapter.EjercicioViewHolder>() {

    inner class EjercicioViewHolder(itemView: android.view.View) :
        RecyclerView.ViewHolder(itemView) {

        private val tvNumero: TextView = itemView.findViewById(R.id.tvNumeroDetalleEjercicio)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreDetalleEjercicio)
        private val tvResumen: TextView = itemView.findViewById(R.id.tvResumenDetalleEjercicio)

        fun bind(ejercicio: EjercicioApi, posicion: Int) {
            tvNumero.text = String.format("%02d", posicion + 1)
            tvNombre.text = ejercicio.nombre
            tvResumen.text =
                "${ejercicio.series} series • ${ejercicio.repeticiones} repeticiones • " +
                        "${ejercicio.descanso} s de descanso"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EjercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ejercicio_detalle, parent, false)
        return EjercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: EjercicioViewHolder, position: Int) {
        holder.bind(ejercicios[position], position)
    }

    override fun getItemCount(): Int = ejercicios.size
}