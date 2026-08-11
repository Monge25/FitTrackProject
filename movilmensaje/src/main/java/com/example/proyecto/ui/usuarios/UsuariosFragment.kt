package com.example.proyecto.ui.usuarios
import com.example.movilmensaje.R

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.mock.UsuariosCatalog
import com.example.proyecto.data.model.Usuario
import com.example.proyecto.ui.usuarios.nuevo.NuevoUsuarioActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class UsuariosFragment : Fragment() {

    private lateinit var rvUsuarios: RecyclerView
    private lateinit var etBuscarUsuario: TextInputEditText
    private lateinit var btnNuevoUsuario: ExtendedFloatingActionButton

    private lateinit var tvTotalUsuarios: TextView
    private lateinit var tvUsuariosActivos: TextView
    private lateinit var tvCantidadResultados: TextView
    private lateinit var tvSinUsuarios: TextView

    private lateinit var adapter: UsuariosAdapter

    private val usuarios = UsuariosCatalog.usuarios

    /*
     * Recibe el resultado de NuevoUsuarioActivity.
     *
     * Por el momento los usuarios se guardan en memoria.
     * Después conectaremos esta parte con la API.
     */
    private val nuevoUsuarioLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { resultado ->

            if (resultado.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }

            val datos = resultado.data
                ?: return@registerForActivityResult

            val nombre = datos.getStringExtra(
                NuevoUsuarioActivity.EXTRA_NOMBRE
            ).orEmpty()

            val correo = datos.getStringExtra(
                NuevoUsuarioActivity.EXTRA_CORREO
            ).orEmpty()

            val rol = datos.getStringExtra(
                NuevoUsuarioActivity.EXTRA_ROL
            ).orEmpty()

            agregarUsuario(
                nombre = nombre,
                correo = correo,
                rol = rol
            )
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_usuarios,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        enlazarVistas(view)

        configurarRecyclerView()

        configurarBuscador()

        configurarBotonNuevo()

        actualizarPantalla(
            usuarios
        )
    }

    private fun enlazarVistas(
        view: View
    ) {

        rvUsuarios = view.findViewById(
            R.id.rvUsuarios
        )

        etBuscarUsuario = view.findViewById(
            R.id.etBuscarUsuario
        )

        btnNuevoUsuario = view.findViewById(
            R.id.btnNuevoUsuario
        )

        tvTotalUsuarios = view.findViewById(
            R.id.tvTotalUsuarios
        )

        tvUsuariosActivos = view.findViewById(
            R.id.tvUsuariosActivos
        )

        tvCantidadResultados = view.findViewById(
            R.id.tvCantidadResultados
        )

        tvSinUsuarios = view.findViewById(
            R.id.tvSinUsuarios
        )
    }

    private fun configurarRecyclerView() {

        adapter = UsuariosAdapter(
            usuarios
        ) { usuario ->

            abrirModalUsuario(
                usuario
            )
        }

        rvUsuarios.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        rvUsuarios.adapter =
            adapter
    }

    private fun configurarBuscador() {

        etBuscarUsuario.addTextChangedListener { editable ->

            val busqueda = editable
                ?.toString()
                ?.trim()
                ?.lowercase()
                .orEmpty()

            filtrarUsuarios(
                busqueda
            )
        }
    }

    private fun configurarBotonNuevo() {

        btnNuevoUsuario.setOnClickListener {

            abrirNuevoUsuario()
        }
    }

    /*
     * Abre la nueva pantalla completa.
     *
     * Ya NO abre UsuarioDialogFragment para crear.
     */
    private fun abrirNuevoUsuario() {

        val intent = Intent(
            requireContext(),
            NuevoUsuarioActivity::class.java
        )

        nuevoUsuarioLauncher.launch(
            intent
        )
    }

    private fun agregarUsuario(
        nombre: String,
        correo: String,
        rol: String
    ) {

        if (
            nombre.isBlank() ||
            correo.isBlank() ||
            rol.isBlank()
        ) {

            Toast.makeText(
                requireContext(),
                "No se recibieron correctamente los datos",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val nuevoId =
            (usuarios.maxOfOrNull { usuario ->
                usuario.id
            } ?: 0) + 1

        val nuevoUsuario = Usuario(
            id = nuevoId,
            nombre = nombre,
            correo = correo,
            rol = rol.uppercase(),
            activo = true
        )

        usuarios.add(
            nuevoUsuario
        )

        limpiarBusqueda()

        actualizarPantalla(
            usuarios
        )

        Toast.makeText(
            requireContext(),
            "Usuario creado correctamente",
            Toast.LENGTH_SHORT
        ).show()
    }

    /*
     * El modal se conserva únicamente para:
     *
     * - Editar usuario.
     * - Cambiar rol.
     * - Activar/desactivar.
     * - Eliminar.
     */
    private fun abrirModalUsuario(
        usuario: Usuario
    ) {

        UsuarioDialogFragment(
            usuario = usuario,

            onGuardar = {
                    usuarioExistente,
                    nombre,
                    correo,
                    _,
                    rol,
                    activo ->

                val indice =
                    usuarios.indexOfFirst {
                        it.id == usuarioExistente?.id
                    }

                if (indice >= 0) {

                    usuarios[indice] =
                        usuarios[indice].copy(
                            nombre = nombre,
                            correo = correo,
                            rol = rol,
                            activo = activo
                        )

                    limpiarBusqueda()

                    actualizarPantalla(
                        usuarios
                    )

                    Toast.makeText(
                        requireContext(),
                        "Usuario actualizado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },

            onEliminar = { usuarioEliminar ->

                usuarios.removeAll {
                    it.id == usuarioEliminar.id
                }

                limpiarBusqueda()

                actualizarPantalla(
                    usuarios
                )

                Toast.makeText(
                    requireContext(),
                    "Usuario eliminado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        ).show(
            childFragmentManager,
            "UsuarioDialog"
        )
    }

    private fun filtrarUsuarios(
        busqueda: String
    ) {

        val usuariosFiltrados =

            if (busqueda.isBlank()) {

                usuarios

            } else {

                usuarios.filter { usuario ->

                    usuario.nombre
                        .lowercase()
                        .contains(busqueda) ||

                            usuario.correo
                                .lowercase()
                                .contains(busqueda) ||

                            usuario.rol
                                .lowercase()
                                .contains(busqueda)
                }
            }

        actualizarPantalla(
            usuariosFiltrados
        )
    }

    private fun limpiarBusqueda() {

        etBuscarUsuario.setText("")
    }

    private fun actualizarPantalla(
        lista: List<Usuario>
    ) {

        adapter.actualizarLista(
            lista
        )

        tvTotalUsuarios.text =
            usuarios.size.toString()

        tvUsuariosActivos.text =
            usuarios.count {
                it.activo
            }.toString()

        tvCantidadResultados.text =
            "${lista.size} resultados"

        if (lista.isEmpty()) {

            tvSinUsuarios.visibility =
                View.VISIBLE

            rvUsuarios.visibility =
                View.GONE

        } else {

            tvSinUsuarios.visibility =
                View.GONE

            rvUsuarios.visibility =
                View.VISIBLE
        }
    }
}