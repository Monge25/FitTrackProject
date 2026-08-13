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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.Usuario
import com.example.proyecto.ui.usuarios.nuevo.NuevoUsuarioActivity
import com.example.proyecto.utils.TokenManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class UsuariosFragment : Fragment() {

    private val viewModel: UsuariosViewModel by viewModels()
    private lateinit var tokenManager: TokenManager
    private lateinit var adapter: UsuariosAdapter

    private lateinit var rvUsuarios: RecyclerView
    private lateinit var etBuscarUsuario: TextInputEditText
    private lateinit var btnNuevoUsuario: ExtendedFloatingActionButton
    private lateinit var tvTotalUsuarios: TextView
    private lateinit var tvUsuariosActivos: TextView
    private lateinit var tvCantidadResultados: TextView
    private lateinit var tvSinUsuarios: TextView

    private val nuevoUsuarioLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode != Activity.RESULT_OK) return@registerForActivityResult

        val datos = resultado.data ?: return@registerForActivityResult
        val nombre   = datos.getStringExtra(NuevoUsuarioActivity.EXTRA_NOMBRE).orEmpty()
        val correo   = datos.getStringExtra(NuevoUsuarioActivity.EXTRA_CORREO).orEmpty()
        val password = datos.getStringExtra(NuevoUsuarioActivity.EXTRA_PASSWORD).orEmpty()
        val rol      = datos.getStringExtra(NuevoUsuarioActivity.EXTRA_ROL).orEmpty()

        lifecycleScope.launch {
            val token = tokenManager.obtenerBearer()
            viewModel.registrarUsuario(token, nombre, correo, password, rol)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_usuarios, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenManager = TokenManager(requireContext())

        enlazarVistas(view)
        configurarRecyclerView()
        configurarBuscador()
        configurarBotonNuevo()
        observarViewModel()
        recargarUsuarios()
    }

    private fun recargarUsuarios() {
        lifecycleScope.launch {
            val token = tokenManager.obtenerBearer()
            viewModel.cargarUsuarios(token)
        }
    }

    private fun enlazarVistas(view: View) {
        rvUsuarios           = view.findViewById(R.id.rvUsuarios)
        etBuscarUsuario      = view.findViewById(R.id.etBuscarUsuario)
        btnNuevoUsuario      = view.findViewById(R.id.btnNuevoUsuario)
        tvTotalUsuarios      = view.findViewById(R.id.tvTotalUsuarios)
        tvUsuariosActivos    = view.findViewById(R.id.tvUsuariosActivos)
        tvCantidadResultados = view.findViewById(R.id.tvCantidadResultados)
        tvSinUsuarios        = view.findViewById(R.id.tvSinUsuarios)
    }

    private fun configurarRecyclerView() {
        adapter = UsuariosAdapter(emptyList()) { usuario ->
            abrirModalUsuario(usuario)
        }
        rvUsuarios.layoutManager = LinearLayoutManager(requireContext())
        rvUsuarios.adapter = adapter
    }

    private fun configurarBuscador() {
        etBuscarUsuario.addTextChangedListener { editable ->
            val busqueda = editable?.toString()?.trim()?.lowercase().orEmpty()
            val lista = viewModel.usuarios.value ?: emptyList()
            val filtrados = if (busqueda.isBlank()) lista else lista.filter {
                val rolTexto = if (it.rol == 0) "administrador" else "operador"
                it.nombre.lowercase().contains(busqueda) ||
                        it.correo.lowercase().contains(busqueda) ||
                        rolTexto.contains(busqueda)
            }
            actualizarPantalla(filtrados)
        }
    }

    private fun configurarBotonNuevo() {
        btnNuevoUsuario.setOnClickListener {
            nuevoUsuarioLauncher.launch(
                Intent(requireContext(), NuevoUsuarioActivity::class.java)
            )
        }
    }

    private fun observarViewModel() {
        viewModel.usuarios.observe(viewLifecycleOwner) { lista ->
            actualizarPantalla(lista)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UsuariosViewModel.UsuariosState.Error ->
                    Toast.makeText(requireContext(), state.mensaje, Toast.LENGTH_LONG).show()
                is UsuariosViewModel.UsuariosState.Exito ->
                    Toast.makeText(requireContext(), state.mensaje, Toast.LENGTH_SHORT).show()
                else -> {}
            }
            if (state !is UsuariosViewModel.UsuariosState.Idle)
                viewModel.resetState()
        }
    }

    private fun abrirModalUsuario(usuario: Usuario) {
        UsuarioDialogFragment(
            usuario = usuario,
            onGuardar = { usuarioExistente, nombre, correo, password, rol, activo ->
                usuarioExistente ?: return@UsuarioDialogFragment
                lifecycleScope.launch {
                    val token = tokenManager.obtenerBearer()
                    viewModel.actualizarUsuario(
                        token    = token,
                        id       = usuarioExistente.id,
                        nombre   = nombre,
                        correo   = correo,
                        rol      = rol,
                        esActivo = activo,
                        password = password.ifBlank { null }
                    )
                }
            },
            onEliminar = { _ ->
                // Por ahora solo recarga — puedes agregar endpoint DELETE después
                Toast.makeText(requireContext(), "Eliminar no disponible aún", Toast.LENGTH_SHORT).show()
            }
        ).show(childFragmentManager, "UsuarioDialog")
    }

    private fun actualizarPantalla(lista: List<Usuario>) {
        val todos = viewModel.usuarios.value ?: emptyList()
        adapter.actualizarLista(lista)
        tvTotalUsuarios.text      = todos.size.toString()
        tvUsuariosActivos.text    = todos.count { it.esActivo }.toString()
        tvCantidadResultados.text = "${lista.size} resultados"
        tvSinUsuarios.visibility  = if (lista.isEmpty()) View.VISIBLE else View.GONE
        rvUsuarios.visibility     = if (lista.isEmpty()) View.GONE else View.VISIBLE
    }
}