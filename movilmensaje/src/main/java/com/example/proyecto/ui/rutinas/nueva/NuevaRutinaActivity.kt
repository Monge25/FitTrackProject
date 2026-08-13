package com.example.proyecto.ui.rutinas.nueva
import com.example.movilmensaje.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.data.mock.RutinasCatalog
import com.example.proyecto.data.model.EjercicioProgramado
import com.example.proyecto.ui.calendario.programar.EjercicioProgramadoAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import androidx.lifecycle.lifecycleScope
import com.example.proyecto.data.model.ActualizarRutinaRequest
import com.example.proyecto.data.model.CrearEjercicioRequest
import com.example.proyecto.data.model.CrearRutinaRequest
import com.example.proyecto.data.repository.RutinasRepository
import com.example.proyecto.utils.TokenManager
import kotlinx.coroutines.launch

class NuevaRutinaActivity : AppCompatActivity() {

    private lateinit var tvTitulo: TextView
    private lateinit var tvSubtitulo: TextView

    private lateinit var etNombreRutina: TextInputEditText
    private lateinit var actvNivelRutina: AutoCompleteTextView
    private lateinit var actvObjetivoRutina: AutoCompleteTextView

    private lateinit var rvEjercicios: RecyclerView
    private lateinit var tvSinEjercicios: TextView

    private lateinit var tvResumenCantidad: TextView
    private lateinit var tvResumenSeries: TextView
    private lateinit var tvResumenDuracion: TextView

    private lateinit var btnAgregarEjercicio: MaterialButton
    private lateinit var btnGuardarRutina: MaterialButton
    private lateinit var btnCancelarRutina: MaterialButton

    private lateinit var adapter: EjercicioProgramadoAdapter

    private val ejercicios =
        mutableListOf<EjercicioProgramado>()

    /** Si viene un id válido, la pantalla entra en modo edición. */
    private val rutinaIdEditar: Int by lazy {
        intent.getIntExtra(EXTRA_RUTINA_ID, -1)
    }

    private val niveles = listOf(
        "Principiante",
        "Intermedio",
        "Avanzado"
    )

    private val objetivos = listOf(
        "Hipertrofia",
        "Fuerza",
        "Resistencia",
        "Cardio",
        "Movilidad"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_nueva_rutina
        )

        inicializarComponentes()

        configurarDropdowns()

        configurarRecyclerView()

        configurarEventos()

        cargarRutinaExistenteSiAplica()
    }

    private fun cargarRutinaExistenteSiAplica() {
        if (rutinaIdEditar == -1) return

        tvTitulo.text = "Editar rutina"
        tvSubtitulo.text = "Modifica los datos de tu rutina."
        btnGuardarRutina.text = "Guardar cambios"

        val nombreExtra = intent.getStringExtra("RUTINA_NOMBRE") ?: ""
        val nivelExtra = intent.getIntExtra("RUTINA_NIVEL", 0)
        val objetivoExtra = intent.getIntExtra("RUTINA_OBJETIVO", 0)

        etNombreRutina.setText(nombreExtra)
        actvNivelRutina.setText(niveles.getOrElse(nivelExtra) { "Principiante" }, false)
        actvObjetivoRutina.setText(objetivos.getOrElse(objetivoExtra) { "Hipertrofia" }, false)

        // Cargar ejercicios existentes desde la API
        lifecycleScope.launch {
            val token = TokenManager(this@NuevaRutinaActivity).obtenerBearer()
            val resultado = RutinasRepository().obtenerPorId(token, rutinaIdEditar)

            resultado.fold(
                onSuccess = { rutina ->
                    ejercicios.clear()
                    ejercicios.addAll(
                        rutina.ejercicios.map { ej ->
                            EjercicioProgramado(
                                id = ej.id.toLong(),
                                nombre = ej.nombre,
                                series = ej.series,
                                repeticiones = ej.repeticiones,
                                pesoKg = ej.peso?.toFloat() ?: 0f,
                                descansoSegundos = ej.descanso,
                                notas = ej.notas ?: ""
                            )
                        }
                    )
                    actualizarPantallaEjercicios()
                },
                onFailure = {
                    Toast.makeText(
                        this@NuevaRutinaActivity,
                        "No se pudieron cargar los ejercicios",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun inicializarComponentes() {

        tvTitulo = findViewById(
            R.id.tvTituloNuevaRutina
        )

        tvSubtitulo = findViewById(
            R.id.tvSubtituloNuevaRutina
        )

        etNombreRutina = findViewById(
            R.id.etNombreRutina
        )

        actvNivelRutina = findViewById(
            R.id.actvNivelRutina
        )

        actvObjetivoRutina = findViewById(
            R.id.actvObjetivoRutina
        )

        rvEjercicios = findViewById(
            R.id.rvEjerciciosRutina
        )

        tvSinEjercicios = findViewById(
            R.id.tvSinEjerciciosRutina
        )

        tvResumenCantidad = findViewById(
            R.id.tvResumenCantidadEjerciciosRutina
        )

        tvResumenSeries = findViewById(
            R.id.tvResumenSeriesRutina
        )

        tvResumenDuracion = findViewById(
            R.id.tvResumenDuracionRutina
        )

        btnAgregarEjercicio = findViewById(
            R.id.btnAgregarEjercicioRutina
        )

        btnGuardarRutina = findViewById(
            R.id.btnGuardarRutina
        )

        btnCancelarRutina = findViewById(
            R.id.btnCancelarRutina
        )
    }

    private fun configurarDropdowns() {

        actvNivelRutina.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                niveles
            )
        )

        actvNivelRutina.setOnClickListener {
            actvNivelRutina.showDropDown()
        }

        actvObjetivoRutina.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                objetivos
            )
        )

        actvObjetivoRutina.setOnClickListener {
            actvObjetivoRutina.showDropDown()
        }
    }

    private fun configurarRecyclerView() {

        adapter = EjercicioProgramadoAdapter(
            ejercicios = mutableListOf(),
            onEditar = { ejercicio ->
                mostrarDialogEjercicio(ejercicio)
            },
            onEliminar = { ejercicio ->
                confirmarEliminarEjercicio(ejercicio)
            }
        )

        rvEjercicios.layoutManager =
            LinearLayoutManager(this)

        rvEjercicios.adapter = adapter
    }

    private fun configurarEventos() {

        btnAgregarEjercicio.setOnClickListener {
            mostrarDialogEjercicio(null)
        }

        btnGuardarRutina.setOnClickListener {
            guardarRutina()
        }

        btnCancelarRutina.setOnClickListener {
            finish()
        }
    }


    // ================= EJERCICIOS =================

    private fun mostrarDialogEjercicio(
        ejercicioExistente: EjercicioProgramado?
    ) {

        val vista = LayoutInflater
            .from(this)
            .inflate(
                R.layout.dialog_agregar_ejercicio,
                null,
                false
            )

        val tvTitulo =
            vista.findViewById<TextView>(
                R.id.tvTituloDialogEjercicio
            )

        val etNombre =
            vista.findViewById<TextInputEditText>(
                R.id.etNombreEjercicioDialog
            )

        val etSeries =
            vista.findViewById<TextInputEditText>(
                R.id.etSeriesEjercicioDialog
            )

        val etRepeticiones =
            vista.findViewById<TextInputEditText>(
                R.id.etRepeticionesEjercicioDialog
            )

        val etPeso =
            vista.findViewById<TextInputEditText>(
                R.id.etPesoEjercicioDialog
            )

        val etDescanso =
            vista.findViewById<TextInputEditText>(
                R.id.etDescansoEjercicioDialog
            )

        val etNotas =
            vista.findViewById<TextInputEditText>(
                R.id.etNotasEjercicioDialog
            )

        if (ejercicioExistente != null) {

            tvTitulo.text = "Editar ejercicio"

            etNombre.setText(ejercicioExistente.nombre)

            etSeries.setText(
                ejercicioExistente.series.toString()
            )

            etRepeticiones.setText(
                ejercicioExistente.repeticiones.toString()
            )

            if (ejercicioExistente.pesoKg > 0f) {
                etPeso.setText(
                    ejercicioExistente.pesoKg.toString()
                )
            }

            etDescanso.setText(
                ejercicioExistente.descansoSegundos.toString()
            )

            etNotas.setText(ejercicioExistente.notas)
        }

        val btnGuardar =
            vista.findViewById<MaterialButton>(
                R.id.btnGuardarEjercicioDialog
            )

        val btnCancelar =
            vista.findViewById<MaterialButton>(
                R.id.btnCancelarEjercicioDialog
            )

        btnGuardar.text =
            if (ejercicioExistente == null) {
                "Agregar ejercicio"
            } else {
                "Guardar cambios"
            }

        val dialog =
            AlertDialog.Builder(this)
                .setView(vista)
                .create()

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnGuardar.setOnClickListener {

            val nombre =
                etNombre.text
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            val series =
                etSeries.text
                    ?.toString()
                    ?.toIntOrNull()

            val repeticiones =
                etRepeticiones.text
                    ?.toString()
                    ?.toIntOrNull()

            val peso =
                etPeso.text
                    ?.toString()
                    ?.toFloatOrNull()
                    ?: 0f

            val descanso =
                etDescanso.text
                    ?.toString()
                    ?.toIntOrNull()

            val notas =
                etNotas.text
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            if (nombre.isBlank()) {
                etNombre.error = "Ingresa el nombre"
                return@setOnClickListener
            }

            if (series == null || series <= 0) {
                etSeries.error = "Ingresa las series"
                return@setOnClickListener
            }

            if (repeticiones == null || repeticiones <= 0) {
                etRepeticiones.error =
                    "Ingresa las repeticiones"
                return@setOnClickListener
            }

            if (descanso == null || descanso < 0) {
                etDescanso.error = "Ingresa el descanso"
                return@setOnClickListener
            }

            if (ejercicioExistente == null) {

                ejercicios.add(
                    EjercicioProgramado(
                        nombre = nombre,
                        series = series,
                        repeticiones = repeticiones,
                        pesoKg = peso,
                        descansoSegundos = descanso,
                        notas = notas
                    )
                )

            } else {

                val indice =
                    ejercicios.indexOfFirst {
                        it.id == ejercicioExistente.id
                    }

                if (indice >= 0) {
                    ejercicios[indice] =
                        ejercicioExistente.copy(
                            nombre = nombre,
                            series = series,
                            repeticiones = repeticiones,
                            pesoKg = peso,
                            descansoSegundos = descanso,
                            notas = notas
                        )
                }
            }

            actualizarPantallaEjercicios()

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun confirmarEliminarEjercicio(
        ejercicio: EjercicioProgramado
    ) {

        AlertDialog.Builder(this)
            .setTitle("Eliminar ejercicio")
            .setMessage(
                "¿Deseas eliminar ${ejercicio.nombre}?"
            )
            .setPositiveButton("Eliminar") { _, _ ->

                ejercicios.removeAll {
                    it.id == ejercicio.id
                }

                actualizarPantallaEjercicios()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarPantallaEjercicios() {
        android.util.Log.d("EJERCICIOS", "Lista tiene: ${ejercicios.size} elementos")
        ejercicios.forEach { android.util.Log.d("EJERCICIOS", "- ${it.nombre}") }

        rvEjercicios.post {
            adapter.actualizarLista(ejercicios.toList())
        }

        val estaVacio = ejercicios.isEmpty()

        tvSinEjercicios.visibility =
            if (estaVacio) View.VISIBLE else View.GONE

        rvEjercicios.visibility =
            if (estaVacio) View.GONE else View.VISIBLE

        val totalSeries =
            ejercicios.sumOf { it.series }

        val duracion = calcularDuracionEstimada()

        tvResumenCantidad.text =
            ejercicios.size.toString()

        tvResumenSeries.text =
            totalSeries.toString()

        tvResumenDuracion.text =
            "$duracion min"
    }

    /** Misma fórmula que usa ProgramarEntrenamientoActivity. */
    private fun calcularDuracionEstimada(): Int {

        if (ejercicios.isEmpty()) {
            return 0
        }

        val segundosTotales =
            ejercicios.sumOf { ejercicio ->

                val tiempoPorSerie =
                    ejercicio.repeticiones * 4

                val tiempoActivo =
                    ejercicio.series * tiempoPorSerie

                val descansos =
                    (ejercicio.series - 1)
                        .coerceAtLeast(0) *
                            ejercicio.descansoSegundos

                tiempoActivo + descansos
            }

        val transiciones =
            (ejercicios.size - 1)
                .coerceAtLeast(0) * 60

        return (
                (segundosTotales + transiciones) / 60f
                )
            .toInt()
            .coerceAtLeast(1)
    }


    // ================= GUARDAR RUTINA =================

    private fun guardarRutina() {
        val nombre = etNombreRutina.text?.toString()?.trim().orEmpty()
        val nivelTexto = actvNivelRutina.text?.toString()?.trim().orEmpty()
        val objetivoTexto = actvObjetivoRutina.text?.toString()?.trim().orEmpty()

        if (nombre.isBlank()) {
            etNombreRutina.error = "Ingresa el nombre de la rutina"
            etNombreRutina.requestFocus()
            return
        }

        if (nivelTexto !in niveles) {
            actvNivelRutina.error = "Selecciona un nivel"
            actvNivelRutina.requestFocus()
            return
        }

        if (objetivoTexto.isBlank()) {
            actvObjetivoRutina.error = "Selecciona un objetivo"
            actvObjetivoRutina.requestFocus()
            return
        }

        if (ejercicios.isEmpty()) {
            Toast.makeText(this, "Agrega al menos un ejercicio", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertir texto a número para la API
        val nivelNum = niveles.indexOf(nivelTexto)
        val objetivoNum = objetivos.indexOf(objetivoTexto)

        val ejerciciosRequest = ejercicios.map { ej ->
            CrearEjercicioRequest(
                nombre = ej.nombre,
                series = ej.series,
                repeticiones = ej.repeticiones,
                peso = if (ej.pesoKg > 0f) ej.pesoKg.toDouble() else null,
                descanso = ej.descansoSegundos,
                notas = ej.notas.ifBlank { null }
            )
        }

        btnGuardarRutina.isEnabled = false

        lifecycleScope.launch {
            val token = TokenManager(this@NuevaRutinaActivity).obtenerBearer()
            val repository = RutinasRepository()

            val resultado = if (rutinaIdEditar != -1) {
                // 1. Actualizar datos de la rutina
                val resultadoRutina = repository.actualizar(
                    token, rutinaIdEditar,
                    ActualizarRutinaRequest(nombre, nivelNum, objetivoNum)
                )

                if (resultadoRutina.isSuccess) {
                    // 2. Sincronizar ejercicios — actualizar los que tienen id real (vinieron de la API)
                    //    y agregar los nuevos (id generado localmente con timestamp, mayor que Int.MAX_VALUE no existe en BD)
                    for (ej in ejercicios) {
                        val tieneIdReal = ej.id <= Int.MAX_VALUE && ej.id > 0 &&
                                ej.id != ej.id.coerceAtMost(System.currentTimeMillis())

                        val request = CrearEjercicioRequest(
                            nombre = ej.nombre,
                            series = ej.series,
                            repeticiones = ej.repeticiones,
                            peso = if (ej.pesoKg > 0f) ej.pesoKg.toDouble() else null,
                            descanso = ej.descansoSegundos,
                            notas = ej.notas.ifBlank { null }
                        )

                        // Si el id cabe en un Int normal es un ejercicio existente en la BD
                        if (ej.id in 1..999999) {
                            repository.actualizarEjercicio(token, rutinaIdEditar, ej.id.toInt(), request)
                        } else {
                            // Es nuevo, lo insertamos
                            repository.agregarEjercicio(token, rutinaIdEditar, request)
                        }
                    }
                    resultadoRutina
                } else {
                    resultadoRutina
                }
            } else {
                repository.crear(
                    token,
                    CrearRutinaRequest(nombre, nivelNum, objetivoNum, ejerciciosRequest)
                )
            }

            resultado.fold(
                onSuccess = {
                    Toast.makeText(
                        this@NuevaRutinaActivity,
                        "Rutina \"$nombre\" guardada",
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                },
                onFailure = { error ->
                    btnGuardarRutina.isEnabled = true
                    Toast.makeText(
                        this@NuevaRutinaActivity,
                        error.message ?: "Error al guardar",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    companion object {
        const val EXTRA_RUTINA_ID = "EXTRA_RUTINA_ID"
    }
}