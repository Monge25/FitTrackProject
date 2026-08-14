package com.example.proyecto.ui.rutinas

import com.example.movilmensaje.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.Rutina
import com.example.proyecto.ui.rutinas.detalle.DetalleRutinaActivity
import com.example.proyecto.ui.rutinas.nueva.NuevaRutinaActivity
import com.example.proyecto.utils.TokenManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RutinasFragment : Fragment() {

    private val viewModel: RutinasViewModel by viewModels()
    private lateinit var tokenManager: TokenManager
    private lateinit var adapterActivas: RutinasAdapter
    private lateinit var adapterDesactivadas: RutinasAdapter

    private lateinit var rootView: View
    private lateinit var rvRutinas: RecyclerView
    private lateinit var rvRutinasDesactivadas: RecyclerView
    private lateinit var tvRutinasActivasVacio: TextView
    private lateinit var tvRutinasDesactivadasVacio: TextView
    private lateinit var btnNuevaRutina: ExtendedFloatingActionButton
    private lateinit var btnVerRutinaDestacada: MaterialButton
    private lateinit var tvRutinaDestacadaNombre: TextView
    private lateinit var tvRutinaDestacadaDescripcion: TextView
    private lateinit var tvCantidadRutinas: TextView

    private val nuevaRutinaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == android.app.Activity.RESULT_OK) {
            recargarRutinas()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_rutinas, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootView = view
        tokenManager = TokenManager(requireContext())

        inicializarComponentes(view)
        configurarRecyclerViews()
        configurarEventos()
        observarViewModel()
        recargarRutinas()
    }

    private fun recargarRutinas() {
        lifecycleScope.launch {
            val token = tokenManager.obtenerBearer()
            viewModel.cargarRutinas(token)
        }
    }

    private fun inicializarComponentes(view: View) {
        rvRutinas = view.findViewById(R.id.rvRutinas)
        rvRutinasDesactivadas = view.findViewById(R.id.rvRutinasDesactivadas)
        tvRutinasActivasVacio = view.findViewById(R.id.tvRutinasActivasVacio)
        tvRutinasDesactivadasVacio = view.findViewById(R.id.tvRutinasDesactivadasVacio)
        btnNuevaRutina = view.findViewById(R.id.btnNuevaRutina)
        btnVerRutinaDestacada = view.findViewById(R.id.btnVerRutinaDestacada)
        tvRutinaDestacadaNombre = view.findViewById(R.id.tvRutinaDestacadaNombre)
        tvRutinaDestacadaDescripcion = view.findViewById(R.id.tvRutinaDestacadaDescripcion)
        tvCantidadRutinas = view.findViewById(R.id.tvCantidadRutinas)
    }

    private fun configurarRecyclerViews() {

        adapterActivas = RutinasAdapter(
            rutinas = emptyList(),
            onRutinaClick = { abrirEditarRutina(it) },
            onToggleActivo = { rutina, activo -> cambiarEstado(rutina, activo) }
        )

        adapterDesactivadas = RutinasAdapter(
            rutinas = emptyList(),
            onRutinaClick = { abrirEditarRutina(it) },
            onToggleActivo = { rutina, activo -> cambiarEstado(rutina, activo) }
        )

        rvRutinas.layoutManager = LinearLayoutManager(requireContext())
        rvRutinas.adapter = adapterActivas

        rvRutinasDesactivadas.layoutManager = LinearLayoutManager(requireContext())
        rvRutinasDesactivadas.adapter = adapterDesactivadas
    }

    private fun configurarEventos() {
        btnNuevaRutina.setOnClickListener { abrirNuevaRutina() }

        btnVerRutinaDestacada.setOnClickListener {
            viewModel.rutinas.value?.firstOrNull { it.esActivo }?.let {
                abrirDetalleRutina(it)
            }
        }
    }

    private fun observarViewModel() {
        viewModel.rutinas.observe(viewLifecycleOwner) { lista ->

            val activas = lista.filter { it.esActivo }
            val desactivadas = lista.filterNot { it.esActivo }

            adapterActivas.actualizarLista(activas)
            adapterDesactivadas.actualizarLista(desactivadas)

            mostrarSeccion(rvRutinas, tvRutinasActivasVacio, activas)
            mostrarSeccion(rvRutinasDesactivadas, tvRutinasDesactivadasVacio, desactivadas)

            tvCantidadRutinas.text = "${activas.size} planes activos"

            val destacada = activas.firstOrNull()
            if (destacada != null) {
                tvRutinaDestacadaNombre.text = destacada.nombre.uppercase()
                tvRutinaDestacadaDescripcion.text = nivelTexto(destacada.nivel)
            } else {
                tvRutinaDestacadaNombre.text = "Sin rutinas"
                tvRutinaDestacadaDescripcion.text = "Crea tu primera rutina"
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RutinasViewModel.RutinasState.Error ->
                    Toast.makeText(requireContext(), state.mensaje, Toast.LENGTH_LONG).show()
                else -> {}
            }
            if (state !is RutinasViewModel.RutinasState.Idle)
                viewModel.resetState()
        }
    }

    private fun mostrarSeccion(
        recyclerView: RecyclerView,
        vacioTextView: TextView,
        lista: List<Rutina>
    ) {
        val estaVacio = lista.isEmpty()
        recyclerView.visibility = if (estaVacio) View.GONE else View.VISIBLE
        vacioTextView.visibility = if (estaVacio) View.VISIBLE else View.GONE
    }

    /**
     * Se llama al mover el Switch de una tarjeta, sea para
     * desactivar (con opción de deshacer vía Snackbar, ya que es más
     * fácil que se toque sin querer) o para reactivar (directo, sin
     * deshacer, ya que basta con volver a apagar el switch).
     */
    private fun cambiarEstado(rutina: Rutina, activo: Boolean) {
        lifecycleScope.launch {
            val token = tokenManager.obtenerBearer()

            if (activo) {
                viewModel.activarRutina(token, rutina.id)

                Snackbar.make(
                    rootView,
                    "\"${rutina.nombre}\" está activa de nuevo",
                    Snackbar.LENGTH_SHORT
                ).show()

            } else {
                viewModel.desactivarRutina(token, rutina.id)

                Snackbar.make(
                    rootView,
                    "\"${rutina.nombre}\" ya no aparece para los clientes",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Deshacer") {
                        lifecycleScope.launch {
                            val tokenDeshacer = tokenManager.obtenerBearer()
                            viewModel.activarRutina(tokenDeshacer, rutina.id)
                        }
                    }
                    .show()
            }
        }
    }

    private fun nivelTexto(nivel: Int) = when (nivel) {
        0 -> "Principiante"
        1 -> "Intermedio"
        else -> "Avanzado"
    }

    private fun abrirNuevaRutina() {
        nuevaRutinaLauncher.launch(
            Intent(requireContext(), NuevaRutinaActivity::class.java)
        )
    }

    private fun abrirEditarRutina(rutina: Rutina) {
        val intent = Intent(requireContext(), NuevaRutinaActivity::class.java)
        intent.putExtra(NuevaRutinaActivity.EXTRA_RUTINA_ID, rutina.id)
        intent.putExtra("RUTINA_NOMBRE", rutina.nombre)
        intent.putExtra("RUTINA_NIVEL", rutina.nivel)
        intent.putExtra("RUTINA_OBJETIVO", rutina.objetivo)
        nuevaRutinaLauncher.launch(intent)
    }

    private fun abrirDetalleRutina(rutina: Rutina) {
        val intent = Intent(requireContext(), DetalleRutinaActivity::class.java)
        intent.putExtra("RUTINA_ID", rutina.id)
        intent.putExtra("RUTINA_NOMBRE", rutina.nombre)
        startActivity(intent)
    }
}