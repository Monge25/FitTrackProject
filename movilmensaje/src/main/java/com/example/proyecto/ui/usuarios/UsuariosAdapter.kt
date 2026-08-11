package com.example.proyecto.ui.usuarios
import com.example.movilmensaje.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.Usuario

class UsuariosAdapter(
    private var usuarios: List<Usuario>,
    private val onUsuarioClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvInicial: TextView =
            itemView.findViewById(R.id.tvInicialUsuario)

        private val tvNombre: TextView =
            itemView.findViewById(R.id.tvNombreUsuario)

        private val tvCorreo: TextView =
            itemView.findViewById(R.id.tvCorreoUsuario)

        private val tvRol: TextView =
            itemView.findViewById(R.id.tvRolUsuarioItem)

        private val tvEstado: TextView =
            itemView.findViewById(R.id.tvEstadoUsuario)

        fun bind(usuario: Usuario) {
            tvInicial.text =
                usuario.nombre.firstOrNull()?.uppercase() ?: "U"

            tvNombre.text = usuario.nombre
            tvCorreo.text = usuario.correo
            tvRol.text = usuario.rol.uppercase()

            tvEstado.text =
                if (usuario.activo) "ACTIVO" else "INACTIVO"

            tvEstado.setTextColor(
                itemView.context.getColor(
                    if (usuario.activo) {
                        android.R.color.holo_green_dark
                    } else {
                        android.R.color.darker_gray
                    }
                )
            )

            itemView.setOnClickListener {
                onUsuarioClick(usuario)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsuarioViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_usuario,
                parent,
                false
            )

        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: UsuarioViewHolder,
        position: Int
    ) {
        holder.bind(usuarios[position])
    }

    override fun getItemCount(): Int = usuarios.size

    fun actualizarLista(nuevaLista: List<Usuario>) {
        usuarios = nuevaLista
        notifyDataSetChanged()
    }
}