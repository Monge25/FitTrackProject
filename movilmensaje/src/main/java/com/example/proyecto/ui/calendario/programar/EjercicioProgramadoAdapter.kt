package com.example.proyecto.ui.calendario.programar
import com.example.movilmensaje.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.EjercicioProgramado
import com.google.android.material.button.MaterialButton
import java.util.Locale

class EjercicioProgramadoAdapter(
    ejercicios: MutableList<EjercicioProgramado>,
    private val onEditar: (EjercicioProgramado) -> Unit,
    private val onEliminar: (EjercicioProgramado) -> Unit
) : RecyclerView.Adapter<
        EjercicioProgramadoAdapter.EjercicioViewHolder
        >() {
    private var ejercicios: MutableList<EjercicioProgramado> = ejercicios.toMutableList()

    inner class EjercicioViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvNumero: TextView =
            itemView.findViewById(R.id.tvNumeroEjercicio)

        private val tvNombre: TextView =
            itemView.findViewById(R.id.tvNombreEjercicioProgramado)

        private val tvResumen: TextView =
            itemView.findViewById(R.id.tvResumenEjercicioProgramado)

        private val tvPeso: TextView =
            itemView.findViewById(R.id.tvPesoEjercicioProgramado)

        private val tvNotas: TextView =
            itemView.findViewById(R.id.tvNotasEjercicioProgramado)

        private val btnEditar: MaterialButton =
            itemView.findViewById(R.id.btnEditarEjercicio)

        private val btnEliminar: MaterialButton =
            itemView.findViewById(R.id.btnEliminarEjercicio)

        fun bind(
            ejercicio: EjercicioProgramado,
            posicion: Int
        ) {
            tvNumero.text =
                String.format(
                    Locale.getDefault(),
                    "%02d",
                    posicion + 1
                )

            tvNombre.text = ejercicio.nombre

            tvResumen.text =
                "${ejercicio.series} series · " +
                        "${ejercicio.repeticiones} repeticiones · " +
                        "${ejercicio.descansoSegundos} s descanso"

            tvPeso.text =
                if (ejercicio.pesoKg > 0f) {
                    "Peso: ${formatearPeso(ejercicio.pesoKg)} kg"
                } else {
                    "Sin peso asignado"
                }

            if (ejercicio.notas.isBlank()) {
                tvNotas.visibility = View.GONE
            } else {
                tvNotas.visibility = View.VISIBLE
                tvNotas.text = ejercicio.notas
            }

            btnEditar.setOnClickListener {
                onEditar(ejercicio)
            }

            btnEliminar.setOnClickListener {
                onEliminar(ejercicio)
            }

            itemView.setOnClickListener {
                onEditar(ejercicio)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EjercicioViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_ejercicio_programado,
                parent,
                false
            )

        return EjercicioViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: EjercicioViewHolder,
        position: Int
    ) {
        holder.bind(
            ejercicios[position],
            position
        )
    }

    override fun getItemCount(): Int =
        ejercicios.size

    fun actualizarLista(nuevaLista: List<EjercicioProgramado>) {
        android.util.Log.d("ADAPTER", "actualizarLista llamado con ${nuevaLista.size} elementos")
        val oldSize = ejercicios.size
        ejercicios = nuevaLista.toMutableList()
        if (oldSize == 0) {
            notifyItemRangeInserted(0, ejercicios.size)
        } else {
            notifyDataSetChanged()
        }
    }

    private fun formatearPeso(
        peso: Float
    ): String {
        return if (peso % 1f == 0f) {
            peso.toInt().toString()
        } else {
            String.format(
                Locale.getDefault(),
                "%.1f",
                peso
            )
        }
    }
}