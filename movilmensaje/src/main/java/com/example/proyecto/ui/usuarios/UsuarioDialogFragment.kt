package com.example.proyecto.ui.usuarios
import com.example.movilmensaje.R

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import com.example.proyecto.data.model.Usuario
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText

/**
 * Este diálogo es solo para EDITAR un usuario existente
 * (UsuariosFragment.abrirModalUsuario siempre lo abre con un usuario
 * ya guardado) — dar de alta uno nuevo pasa por NuevoUsuarioActivity,
 * una pantalla aparte. Por eso aquí no hay campo de contraseña: un
 * administrador no puede cambiarle la contraseña a otra cuenta desde
 * esta pantalla.
 */
class UsuarioDialogFragment(
    private val usuario: Usuario? = null,
    private val onGuardar: (
        usuarioExistente: Usuario?,
        nombre: String,
        correo: String,
        rol: String,
        activo: Boolean
    ) -> Unit,
    private val onEliminar: (Usuario) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {

        val view = LayoutInflater
            .from(requireContext())
            .inflate(R.layout.dialog_usuario, null)

        val etNombre =
            view.findViewById<TextInputEditText>(
                R.id.etNombreUsuarioDialog
            )

        val etCorreo =
            view.findViewById<TextInputEditText>(
                R.id.etCorreoUsuarioDialog
            )

        val rbAdministrador =
            view.findViewById<RadioButton>(
                R.id.rbAdministrador
            )

        val rbOperador =
            view.findViewById<RadioButton>(
                R.id.rbOperador
            )

        val switchActivo =
            view.findViewById<SwitchMaterial>(
                R.id.switchUsuarioActivo
            )

        val btnGuardar =
            view.findViewById<MaterialButton>(
                R.id.btnGuardarUsuarioDialog
            )

        val btnEliminar =
            view.findViewById<MaterialButton>(
                R.id.btnEliminarUsuarioDialog
            )

        val btnCancelar =
            view.findViewById<MaterialButton>(
                R.id.btnCancelarUsuarioDialog
            )

        val tvTitulo =
            view.findViewById<android.widget.TextView>(
                R.id.tvTituloDialogUsuario
            )

        if (usuario != null) {
            tvTitulo.text = "Editar usuario"

            etNombre.setText(usuario.nombre)
            etCorreo.setText(usuario.correo)

            rbAdministrador.isChecked = usuario.rol == 0

            rbOperador.isChecked =
                !rbAdministrador.isChecked

            switchActivo.isChecked =
                usuario.esActivo

            btnEliminar.visibility =
                View.VISIBLE
        }

        btnGuardar.setOnClickListener {
            val nombre =
                etNombre.text?.toString()?.trim().orEmpty()

            val correo =
                etCorreo.text?.toString()?.trim().orEmpty()

            val rol =
                if (rbAdministrador.isChecked) {
                    "ADMINISTRADOR"
                } else {
                    "OPERADOR"
                }

            if (nombre.isBlank()) {
                etNombre.error = "Ingresa el nombre"
                return@setOnClickListener
            }

            if (
                correo.isBlank() ||
                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(correo)
                    .matches()
            ) {
                etCorreo.error = "Ingresa un correo válido"
                return@setOnClickListener
            }

            onGuardar(
                usuario,
                nombre,
                correo,
                rol,
                switchActivo.isChecked
            )

            dismiss()
        }

        btnEliminar.setOnClickListener {
            val usuarioActual = usuario ?: return@setOnClickListener

            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar usuario")
                .setMessage(
                    "¿Deseas eliminar a ${usuarioActual.nombre}?"
                )
                .setPositiveButton("Eliminar") { _, _ ->
                    onEliminar(usuarioActual)
                    dismiss()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnCancelar.setOnClickListener {
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog?.window?.setBackgroundDrawableResource(
            android.R.color.transparent
        )
    }
}