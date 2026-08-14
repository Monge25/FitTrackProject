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
import kotlinx.coroutines.launch

class RutinasFragment : Fragment() {

    private val viewModel: RutinasViewModel by viewModels()
    private lateinit var tokenManager: TokenManager
    private lateinit var adapter: RutinasAdapter

    private lateinit var rvRutinas: RecyclerView
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

        tokenManager = TokenManager(requireContext())

        inicializarComponentes(view)
        configurarRecyclerView()
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
        btnNuevaRutina = view.findViewById(R.id.btnNuevaRutina)
        btnVerRutinaDestacada = view.findViewById(R.id.btnVerRutinaDestacada)
        tvRutinaDestacadaNombre = view.findViewById(R.id.tvRutinaDestacadaNombre)
        tvRutinaDestacadaDescripcion = view.findViewById(R.id.tvRutinaDestacadaDescripcion)
        tvCantidadRutinas = view.findViewById(R.id.tvCantidadRutinas)
    }

    private fun configurarRecyclerView() {
        adapter = RutinasAdapter(emptyList()) { rutina ->
            abrirEditarRutina(rutina)
        }
        rvRutinas.layoutManager = LinearLayoutManager(requireContext())
        rvRutinas.adapter = adapter
    }

    private fun configurarEventos() {
        btnNuevaRutina.setOnClickListener { abrirNuevaRutina() }

        btnVerRutinaDestacada.setOnClickListener {
            viewModel.rutinas.value?.firstOrNull()?.let {
                abrirDetalleRutina(it)
            }
        }
    }

    private fun observarViewModel() {
        viewModel.rutinas.observe(viewLifecycleOwner) { lista ->
            adapter.actualizarLista(lista)

            // El RecyclerView vive dentro de un ScrollView con altura
            // "wrap_content"; cuando los datos llegan después del
            // primer dibujado (como aquí, que vienen del backend de
            // forma asíncrona), se queda con el tamaño viejo si no se
            // le pide explícitamente que se vuelva a medir.
            rvRutinas.post {
                rvRutinas.requestLayout()
            }

            tvCantidadRutinas.text = "${lista.size} planes disponibles"

            val destacada = lista.firstOrNull()
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
                is RutinasViewModel.RutinasState.DesactivadaExitosa ->
                    Toast.makeText(requireContext(), state.mensaje, Toast.LENGTH_SHORT).show()
                else -> {}
            }
            if (state !is RutinasViewModel.RutinasState.Idle)
                viewModel.resetState()
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