package com.example.proyecto.ui.rutinas
import com.example.movilmensaje.R

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
import com.example.proyecto.data.mock.RutinasCatalog
import com.example.proyecto.data.model.Rutina
import com.example.proyecto.ui.rutinas.detalle.DetalleRutinaActivity
import com.example.proyecto.ui.rutinas.nueva.NuevaRutinaActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class RutinasFragment : Fragment() {

    private lateinit var rvRutinas: RecyclerView

    private lateinit var btnNuevaRutina:
            ExtendedFloatingActionButton

    private lateinit var btnVerRutinaDestacada:
            MaterialButton

    private lateinit var tvRutinaDestacadaNombre:
            TextView

    private lateinit var tvRutinaDestacadaDescripcion:
            TextView

    private lateinit var tvCantidadRutinas:
            TextView

    private lateinit var adapter:
            RutinasAdapter

    private var rutinas:
            List<Rutina> = emptyList()

    private val nuevaRutinaLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { resultado ->

            if (resultado.resultCode == android.app.Activity.RESULT_OK) {
                cargarRutinas()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_rutinas,
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

        inicializarComponentes(view)

        configurarRecyclerView()

        cargarRutinas()

        configurarEventos()
    }


    private fun inicializarComponentes(
        view: View
    ) {

        rvRutinas =
            view.findViewById(
                R.id.rvRutinas
            )

        btnNuevaRutina =
            view.findViewById(
                R.id.btnNuevaRutina
            )

        btnVerRutinaDestacada =
            view.findViewById(
                R.id.btnVerRutinaDestacada
            )

        tvRutinaDestacadaNombre =
            view.findViewById(
                R.id.tvRutinaDestacadaNombre
            )

        tvRutinaDestacadaDescripcion =
            view.findViewById(
                R.id.tvRutinaDestacadaDescripcion
            )

        tvCantidadRutinas =
            view.findViewById(
                R.id.tvCantidadRutinas
            )
    }


    private fun configurarRecyclerView() {

        adapter = RutinasAdapter(
            emptyList()
        ) { rutina: Rutina ->

            abrirEditarRutina(
                rutina
            )
        }

        rvRutinas.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        rvRutinas.adapter =
            adapter
    }


    private fun cargarRutinas() {

        rutinas = RutinasCatalog.rutinas

        adapter.actualizarLista(
            rutinas
        )

        tvCantidadRutinas.text =
            "${rutinas.size} planes disponibles"

        val rutinaDestacada =
            rutinas.firstOrNull()

        if (rutinaDestacada != null) {

            tvRutinaDestacadaNombre.text =
                rutinaDestacada.nombre.uppercase()

            tvRutinaDestacadaDescripcion.text =
                rutinaDestacada.descripcion

        } else {

            tvRutinaDestacadaNombre.text =
                "Sin rutinas"

            tvRutinaDestacadaDescripcion.text =
                "Crea tu primera rutina"
        }
    }


    private fun configurarEventos() {

        btnNuevaRutina.setOnClickListener {

            abrirNuevaRutina()
        }


        btnVerRutinaDestacada.setOnClickListener {

            val rutinaDestacada =
                rutinas.firstOrNull()

            if (rutinaDestacada != null) {

                abrirDetalleRutina(
                    rutinaDestacada
                )
            }
        }
    }


    private fun abrirDetalleRutina(
        rutina: Rutina
    ) {

        val intent = Intent(
            requireContext(),
            DetalleRutinaActivity::class.java
        )

        intent.putExtra(
            "RUTINA_ID",
            rutina.id
        )

        intent.putExtra(
            "RUTINA_NOMBRE",
            rutina.nombre
        )

        intent.putExtra(
            "RUTINA_DESCRIPCION",
            rutina.descripcion
        )

        intent.putExtra(
            "RUTINA_NIVEL",
            rutina.nivel
        )

        intent.putExtra(
            "RUTINA_DURACION",
            rutina.duracion
        )

        intent.putExtra(
            "RUTINA_EJERCICIOS",
            rutina.ejercicios
        )

        startActivity(intent)
    }


    private fun abrirEditarRutina(
        rutina: Rutina
    ) {

        val intent = Intent(
            requireContext(),
            NuevaRutinaActivity::class.java
        )

        intent.putExtra(
            NuevaRutinaActivity.EXTRA_RUTINA_ID,
            rutina.id
        )

        nuevaRutinaLauncher.launch(intent)
    }

    private fun abrirNuevaRutina() {

        val intent = Intent(
            requireContext(),
            NuevaRutinaActivity::class.java
        )

        nuevaRutinaLauncher.launch(intent)
    }
}