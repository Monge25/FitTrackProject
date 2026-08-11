package com.example.proyecto.ui.calendario
import com.example.movilmensaje.R

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.model.EntrenamientoProgramado
import com.example.proyecto.data.repository.CalendarioRepository
import com.example.proyecto.ui.calendario.programar.ProgramarEntrenamientoActivity
import com.example.proyecto.ui.rutinas.detalle.DetalleRutinaActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class CalendarioFragment : Fragment() {

    private lateinit var rvCalendario: RecyclerView
    private lateinit var tvResumenCalendario: TextView
    private lateinit var tvResumenCompletados: TextView
    private lateinit var tvCalendarioVacio: TextView
    private lateinit var btnProgramarEntrenamiento:
            ExtendedFloatingActionButton

    private lateinit var repository:
            CalendarioRepository

    private lateinit var adapter:
            CalendarioAdapter

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

        return inflater.inflate(
            R.layout.fragment_calendario,
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

        repository =
            CalendarioRepository(
                requireContext()
            )

        enlazarVistas(view)

        configurarRecyclerView()

        configurarEventos()

        cargarEntrenamientos()
    }

    override fun onResume() {
        super.onResume()

        if (this::repository.isInitialized) {
            cargarEntrenamientos()
        }
    }

    private fun enlazarVistas(
        view: View
    ) {

        rvCalendario =
            view.findViewById(
                R.id.rvCalendario
            )

        tvResumenCalendario =
            view.findViewById(
                R.id.tvResumenCalendario
            )

        tvResumenCompletados =
            view.findViewById(
                R.id.tvResumenCompletados
            )

        tvCalendarioVacio =
            view.findViewById(
                R.id.tvCalendarioVacio
            )

        btnProgramarEntrenamiento =
            view.findViewById(
                R.id.btnProgramarEntrenamiento
            )
    }

    private fun configurarRecyclerView() {

        adapter = CalendarioAdapter(
            emptyList()
        ) { entrenamiento ->

            abrirDetalleEntrenamiento(
                entrenamiento
            )
        }

        rvCalendario.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        rvCalendario.adapter =
            adapter
    }

    private fun configurarEventos() {

        btnProgramarEntrenamiento
            .setOnClickListener {

                val intent = Intent(
                    requireContext(),
                    ProgramarEntrenamientoActivity::class.java
                )

                programarLauncher.launch(
                    intent
                )
            }
    }

    private fun cargarEntrenamientos() {

        val entrenamientos =
            repository.obtenerEntrenamientos()

        adapter.actualizarLista(
            entrenamientos
        )

        val completados =
            entrenamientos.count {
                it.completado
            }

        tvResumenCalendario.text =
            "${entrenamientos.size} entrenamientos programados"

        tvResumenCompletados.text =
            "$completados completados"

        val estaVacio =
            entrenamientos.isEmpty()

        rvCalendario.visibility =
            if (estaVacio) {
                View.GONE
            } else {
                View.VISIBLE
            }

        tvCalendarioVacio.visibility =
            if (estaVacio) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun abrirDetalleEntrenamiento(
        entrenamiento: EntrenamientoProgramado
    ) {

        val intent = Intent(
            requireContext(),
            DetalleRutinaActivity::class.java
        )

        intent.putExtra(
            "RUTINA_ID",
            entrenamiento.rutinaId
        )

        intent.putExtra(
            "RUTINA_NOMBRE",
            entrenamiento.nombreRutina
        )

        intent.putExtra(
            "RUTINA_DESCRIPCION",
            entrenamiento.descripcion
        )

        intent.putExtra(
            "RUTINA_NIVEL",
            entrenamiento.nivel
        )

        intent.putExtra(
            "RUTINA_DURACION",
            entrenamiento.duracionMinutos
        )

        intent.putExtra(
            "RUTINA_EJERCICIOS",
            entrenamiento.cantidadEjercicios
        )

        intent.putExtra(
            "ENTRENAMIENTO_PROGRAMADO_ID",
            entrenamiento.id
        )

        intent.putExtra(
            "ENTRENAMIENTO_FECHA",
            entrenamiento.fecha
        )

        intent.putExtra(
            "ENTRENAMIENTO_HORA",
            entrenamiento.hora
        )

        intent.putExtra(
            "VIENE_DE_CALENDARIO",
            true
        )

        startActivity(
            intent
        )
    }

    /*
     * Puedes llamar este método después desde un botón de opciones.
     * Ya no se abre automáticamente al tocar la tarjeta, porque ahora
     * tocar la tarjeta abre el detalle.
     */
    private fun mostrarOpcionesEntrenamiento(
        entrenamiento: EntrenamientoProgramado
    ) {

        val opciones = arrayOf(
            if (entrenamiento.completado) {
                "Marcar como pendiente"
            } else {
                "Marcar como completado"
            },
            "Eliminar del calendario"
        )

        AlertDialog.Builder(
            requireContext()
        )
            .setTitle(
                entrenamiento.nombreRutina
            )
            .setMessage(
                "${entrenamiento.fecha}, " +
                        "${entrenamiento.hora}\n" +
                        "${entrenamiento.duracionMinutos} minutos"
            )
            .setItems(
                opciones
            ) { _, posicion ->

                when (posicion) {

                    0 -> {
                        repository.cambiarEstado(
                            entrenamiento.id
                        )
                    }

                    1 -> {
                        repository.eliminarEntrenamiento(
                            entrenamiento.id
                        )
                    }
                }

                cargarEntrenamientos()
            }
            .setNegativeButton(
                "Cerrar",
                null
            )
            .show()
    }
}