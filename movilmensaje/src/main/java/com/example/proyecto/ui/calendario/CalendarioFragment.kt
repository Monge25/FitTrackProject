package com.example.proyecto.ui.calendario
import com.example.movilmensaje.R

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.EntrenamientoProgramado
import com.example.proyecto.data.repository.CalendarioRepository
import com.example.proyecto.data.repository.RutinasRepository
import com.example.proyecto.ui.calendario.programar.ProgramarEntrenamientoActivity
import com.example.proyecto.ui.rutinas.detalle.DetalleRutinaActivity
import com.example.proyecto.utils.TokenManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch

class CalendarioFragment : Fragment() {

    private lateinit var rvCalendario: RecyclerView
    private lateinit var rvCalendarioCumplidos: RecyclerView
    private lateinit var rvCalendarioVencidos: RecyclerView

    private lateinit var tvResumenCalendario: TextView
    private lateinit var tvResumenCompletados: TextView

    private lateinit var tvCalendarioVacio: TextView
    private lateinit var tvCalendarioCumplidosVacio: TextView
    private lateinit var tvCalendarioVencidosVacio: TextView

    private lateinit var btnProgramarEntrenamiento: ExtendedFloatingActionButton

    private lateinit var repository: CalendarioRepository
    private val rutinasRepository = RutinasRepository()
    private lateinit var tokenManager: TokenManager

    private lateinit var adapterProximos: CalendarioAdapter
    private lateinit var adapterCumplidos: CalendarioAdapter
    private lateinit var adapterVencidos: CalendarioAdapter

    private val programarLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            cargarEntrenamientos()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_calendario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = CalendarioRepository(requireContext())
        tokenManager = TokenManager(requireContext())

        enlazarVistas(view)
        configurarRecyclerViews()
        configurarEventos()
        cargarEntrenamientos()
    }

    override fun onResume() {
        super.onResume()

        if (this::repository.isInitialized) {
            cargarEntrenamientos()
        }
    }

    private fun enlazarVistas(view: View) {

        rvCalendario = view.findViewById(R.id.rvCalendario)
        rvCalendarioCumplidos = view.findViewById(R.id.rvCalendarioCumplidos)
        rvCalendarioVencidos = view.findViewById(R.id.rvCalendarioVencidos)

        tvResumenCalendario = view.findViewById(R.id.tvResumenCalendario)
        tvResumenCompletados = view.findViewById(R.id.tvResumenCompletados)

        tvCalendarioVacio = view.findViewById(R.id.tvCalendarioVacio)
        tvCalendarioCumplidosVacio = view.findViewById(R.id.tvCalendarioCumplidosVacio)
        tvCalendarioVencidosVacio = view.findViewById(R.id.tvCalendarioVencidosVacio)

        btnProgramarEntrenamiento = view.findViewById(R.id.btnProgramarEntrenamiento)
    }

    private fun configurarRecyclerViews() {

        adapterProximos = CalendarioAdapter(emptyList()) { abrirDetalleEntrenamiento(it) }
        adapterCumplidos = CalendarioAdapter(emptyList()) { abrirDetalleEntrenamiento(it) }
        adapterVencidos = CalendarioAdapter(emptyList()) { abrirDetalleEntrenamiento(it) }

        rvCalendario.layoutManager = LinearLayoutManager(requireContext())
        rvCalendario.adapter = adapterProximos

        rvCalendarioCumplidos.layoutManager = LinearLayoutManager(requireContext())
        rvCalendarioCumplidos.adapter = adapterCumplidos

        rvCalendarioVencidos.layoutManager = LinearLayoutManager(requireContext())
        rvCalendarioVencidos.adapter = adapterVencidos
    }

    private fun configurarEventos() {

        btnProgramarEntrenamiento.setOnClickListener {
            val intent = Intent(requireContext(), ProgramarEntrenamientoActivity::class.java)
            programarLauncher.launch(intent)
        }
    }

    /**
     * Carga los entrenamientos programados (guardados en el
     * teléfono) y los filtra para quedarse solo con los que
     * pertenecen a una rutina que TODAVÍA existe y está activa en la
     * base de datos real — si borraste o desactivaste una rutina
     * después de haberla programado, ese entrenamiento ya no
     * aparece. Después los separa en Próximos / Cumplidos / Vencidos.
     */
    private fun cargarEntrenamientos() {

        lifecycleScope.launch {

            val token = tokenManager.obtenerBearer()

            val idsRutinasValidas: Set<Int> =
                rutinasRepository.obtenerTodas(token).fold(
                    onSuccess = { rutinas ->
                        rutinas.filter { it.esActivo }.map { it.id }.toSet()
                    },
                    onFailure = {
                        // Sin conexión: no se puede confirmar cuáles
                        // siguen existiendo, así que por seguridad no
                        // se muestra ninguno en vez de mostrar datos
                        // que ya no son válidos.
                        emptySet()
                    }
                )

            val entrenamientosValidos =
                repository.obtenerEntrenamientos()
                    .filter { it.rutinaId in idsRutinasValidas }

            actualizarSecciones(entrenamientosValidos)
        }
    }

    private fun actualizarSecciones(entrenamientos: List<EntrenamientoProgramado>) {

        val proximos = entrenamientos.filter {
            it.calcularEstado() == EstadoEntrenamiento.PROXIMA
        }

        val cumplidos = entrenamientos.filter {
            it.calcularEstado() == EstadoEntrenamiento.CUMPLIDA
        }

        val vencidos = entrenamientos.filter {
            it.calcularEstado() == EstadoEntrenamiento.VENCIDA
        }

        adapterProximos.actualizarLista(proximos)
        adapterCumplidos.actualizarLista(cumplidos)
        adapterVencidos.actualizarLista(vencidos)

        mostrarSeccion(rvCalendario, tvCalendarioVacio, proximos)
        mostrarSeccion(rvCalendarioCumplidos, tvCalendarioCumplidosVacio, cumplidos)
        mostrarSeccion(rvCalendarioVencidos, tvCalendarioVencidosVacio, vencidos)

        tvResumenCalendario.text = "${entrenamientos.size} entrenamientos programados"
        tvResumenCompletados.text = "${cumplidos.size} completados"
    }

    private fun mostrarSeccion(
        recyclerView: RecyclerView,
        vacioTextView: TextView,
        lista: List<EntrenamientoProgramado>
    ) {
        val estaVacio = lista.isEmpty()
        recyclerView.visibility = if (estaVacio) View.GONE else View.VISIBLE
        vacioTextView.visibility = if (estaVacio) View.VISIBLE else View.GONE
    }

    private fun abrirDetalleEntrenamiento(entrenamiento: EntrenamientoProgramado) {

        val intent = Intent(requireContext(), DetalleRutinaActivity::class.java)

        intent.putExtra("RUTINA_ID", entrenamiento.rutinaId)
        intent.putExtra("RUTINA_NOMBRE", entrenamiento.nombreRutina)
        intent.putExtra("RUTINA_DESCRIPCION", entrenamiento.descripcion)
        intent.putExtra("RUTINA_NIVEL", entrenamiento.nivel)
        intent.putExtra("RUTINA_DURACION", entrenamiento.duracionMinutos)
        intent.putExtra("RUTINA_EJERCICIOS", entrenamiento.cantidadEjercicios)
        intent.putExtra("ENTRENAMIENTO_PROGRAMADO_ID", entrenamiento.id)
        intent.putExtra("ENTRENAMIENTO_FECHA", entrenamiento.fecha)
        intent.putExtra("ENTRENAMIENTO_HORA", entrenamiento.hora)
        intent.putExtra("VIENE_DE_CALENDARIO", true)

        startActivity(intent)
    }
}