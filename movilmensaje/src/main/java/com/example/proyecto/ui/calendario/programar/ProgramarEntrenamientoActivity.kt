package com.example.proyecto.ui.calendario.programar
import com.example.movilmensaje.R

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.proyecto.data.model.EntrenamientoProgramado
import com.example.proyecto.data.repository.CalendarioRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProgramarEntrenamientoActivity :
    AppCompatActivity() {

    private lateinit var actvRutina:
            AutoCompleteTextView

    private lateinit var actvObjetivo:
            AutoCompleteTextView

    private lateinit var etFecha:
            TextInputEditText

    private lateinit var etHora:
            TextInputEditText

    private lateinit var rvEjercicios:
            RecyclerView

    private lateinit var tvSinEjercicios:
            TextView

    private lateinit var tvResumenCantidad:
            TextView

    private lateinit var tvResumenSeries:
            TextView

    private lateinit var tvResumenDuracion:
            TextView

    private lateinit var btnAgregarEjercicio:
            MaterialButton

    private lateinit var btnGuardar:
            MaterialButton

    private lateinit var btnCancelar:
            MaterialButton

    private lateinit var adapter:
            EjercicioProgramadoAdapter

    private val ejercicios =
        mutableListOf<EjercicioProgramado>()

    private val calendarioSeleccionado =
        Calendar.getInstance()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_programar_entrenamiento
        )

        enlazarVistas()
        configurarSelectores()
        configurarRecyclerView()
        configurarEventos()
        actualizarPantallaEjercicios()
        configurarFechaInicial()
    }

    private fun enlazarVistas() {

        actvRutina =
            findViewById(
                R.id.actvRutinaProgramada
            )

        actvObjetivo =
            findViewById(
                R.id.actvObjetivoEntrenamiento
            )

        etFecha =
            findViewById(
                R.id.etFechaProgramada
            )

        etHora =
            findViewById(
                R.id.etHoraProgramada
            )

        rvEjercicios =
            findViewById(
                R.id.rvEjerciciosProgramados
            )

        tvSinEjercicios =
            findViewById(
                R.id.tvSinEjerciciosProgramados
            )

        tvResumenCantidad =
            findViewById(
                R.id.tvResumenCantidadEjercicios
            )

        tvResumenSeries =
            findViewById(
                R.id.tvResumenSeries
            )

        tvResumenDuracion =
            findViewById(
                R.id.tvResumenDuracion
            )

        btnAgregarEjercicio =
            findViewById(
                R.id.btnAgregarEjercicio
            )

        btnGuardar =
            findViewById(
                R.id.btnGuardarEntrenamientoProgramado
            )

        btnCancelar =
            findViewById(
                R.id.btnCancelarEntrenamientoProgramado
            )
    }

    private fun configurarSelectores() {

        val rutinas =
            RutinasCatalog.rutinas.map { it.nombre } +
                    RutinasCatalog.rutinaPersonalizada.nombre

        val objetivos = listOf(
            "Hipertrofia",
            "Fuerza",
            "Resistencia",
            "Cardio",
            "Movilidad"
        )

        actvRutina.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                rutinas
            )
        )

        actvObjetivo.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                objetivos
            )
        )

        actvRutina.setOnClickListener {
            actvRutina.showDropDown()
        }

        actvObjetivo.setOnClickListener {
            actvObjetivo.showDropDown()
        }
    }

    private fun configurarRecyclerView() {

        adapter =
            EjercicioProgramadoAdapter(
                ejercicios = emptyList(),
                onEditar = { ejercicio ->
                    mostrarDialogEjercicio(
                        ejercicio
                    )
                },
                onEliminar = { ejercicio ->
                    confirmarEliminarEjercicio(
                        ejercicio
                    )
                }
            )

        rvEjercicios.layoutManager =
            LinearLayoutManager(this)

        rvEjercicios.adapter =
            adapter

        rvEjercicios.isNestedScrollingEnabled =
            false
    }

    private fun configurarEventos() {

        etFecha.setOnClickListener {
            mostrarSelectorFecha()
        }

        etHora.setOnClickListener {
            mostrarSelectorHora()
        }

        btnAgregarEjercicio.setOnClickListener {
            mostrarDialogEjercicio(null)
        }

        btnGuardar.setOnClickListener {
            guardarEntrenamiento()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun configurarFechaInicial() {

        calendarioSeleccionado.add(
            Calendar.DAY_OF_MONTH,
            1
        )

        actualizarTextoFecha()

        val horaActual =
            Calendar.getInstance()

        etHora.setText(
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                horaActual.get(Calendar.HOUR_OF_DAY),
                horaActual.get(Calendar.MINUTE)
            )
        )
    }

    private fun mostrarSelectorFecha() {

        DatePickerDialog(
            this,
            { _, year, month, day ->

                calendarioSeleccionado.set(
                    Calendar.YEAR,
                    year
                )

                calendarioSeleccionado.set(
                    Calendar.MONTH,
                    month
                )

                calendarioSeleccionado.set(
                    Calendar.DAY_OF_MONTH,
                    day
                )

                actualizarTextoFecha()
            },
            calendarioSeleccionado.get(
                Calendar.YEAR
            ),
            calendarioSeleccionado.get(
                Calendar.MONTH
            ),
            calendarioSeleccionado.get(
                Calendar.DAY_OF_MONTH
            )
        ).apply {

            datePicker.minDate =
                System.currentTimeMillis() - 1000

        }.show()
    }

    private fun actualizarTextoFecha() {

        val formato =
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            )

        etFecha.setText(
            formato.format(
                calendarioSeleccionado.time
            )
        )
    }

    private fun mostrarSelectorHora() {

        val ahora =
            Calendar.getInstance()

        TimePickerDialog(
            this,
            { _, hora, minuto ->

                etHora.setText(
                    String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        hora,
                        minuto
                    )
                )
            },
            ahora.get(Calendar.HOUR_OF_DAY),
            ahora.get(Calendar.MINUTE),
            true
        ).show()
    }

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

            tvTitulo.text =
                "Editar ejercicio"

            etNombre.setText(
                ejercicioExistente.nombre
            )

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
                ejercicioExistente
                    .descansoSegundos
                    .toString()
            )

            etNotas.setText(
                ejercicioExistente.notas
            )
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
                etNombre.error =
                    "Ingresa el nombre"
                return@setOnClickListener
            }

            if (series == null || series <= 0) {
                etSeries.error =
                    "Ingresa las series"
                return@setOnClickListener
            }

            if (
                repeticiones == null ||
                repeticiones <= 0
            ) {
                etRepeticiones.error =
                    "Ingresa las repeticiones"
                return@setOnClickListener
            }

            if (
                descanso == null ||
                descanso < 0
            ) {
                etDescanso.error =
                    "Ingresa el descanso"
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
                        it.id ==
                                ejercicioExistente.id
                    }

                if (indice >= 0) {
                    ejercicios[indice] =
                        ejercicioExistente.copy(
                            nombre = nombre,
                            series = series,
                            repeticiones =
                                repeticiones,
                            pesoKg = peso,
                            descansoSegundos =
                                descanso,
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
            .setTitle(
                "Eliminar ejercicio"
            )
            .setMessage(
                "¿Deseas eliminar ${ejercicio.nombre}?"
            )
            .setPositiveButton(
                "Eliminar"
            ) { _, _ ->

                ejercicios.removeAll {
                    it.id == ejercicio.id
                }

                actualizarPantallaEjercicios()
            }
            .setNegativeButton(
                "Cancelar",
                null
            )
            .show()
    }

    private fun actualizarPantallaEjercicios() {

        adapter.actualizarLista(
            ejercicios.toList()
        )

        val estaVacio =
            ejercicios.isEmpty()

        tvSinEjercicios.visibility =
            if (estaVacio) {
                View.VISIBLE
            } else {
                View.GONE
            }

        rvEjercicios.visibility =
            if (estaVacio) {
                View.GONE
            } else {
                View.VISIBLE
            }

        val totalSeries =
            ejercicios.sumOf {
                it.series
            }

        val duracion =
            calcularDuracionEstimada()

        tvResumenCantidad.text =
            ejercicios.size.toString()

        tvResumenSeries.text =
            totalSeries.toString()

        tvResumenDuracion.text =
            "$duracion min"
    }

    private fun calcularDuracionEstimada(): Int {

        if (ejercicios.isEmpty()) {
            return 0
        }

        val segundosTotales =
            ejercicios.sumOf { ejercicio ->

                val tiempoPorSerie =
                    ejercicio.repeticiones * 4

                val tiempoActivo =
                    ejercicio.series *
                            tiempoPorSerie

                val descansos =
                    (ejercicio.series - 1)
                        .coerceAtLeast(0) *
                            ejercicio.descansoSegundos

                tiempoActivo + descansos
            }

        // Añadimos tiempo para cambiar de ejercicio.
        val transiciones =
            (ejercicios.size - 1)
                .coerceAtLeast(0) * 60

        return (
                (segundosTotales + transiciones) / 60f
                )
            .toInt()
            .coerceAtLeast(1)
    }

    private fun guardarEntrenamiento() {

        val nombreRutina =
            actvRutina.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val objetivo =
            actvObjetivo.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val fecha =
            etFecha.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val hora =
            etHora.text
                ?.toString()
                ?.trim()
                .orEmpty()

        if (nombreRutina.isBlank()) {
            actvRutina.error =
                "Selecciona una rutina"
            return
        }

        if (objetivo.isBlank()) {
            actvObjetivo.error =
                "Selecciona un objetivo"
            return
        }

        if (fecha.isBlank()) {
            etFecha.error =
                "Selecciona una fecha"
            return
        }

        if (hora.isBlank()) {
            etHora.error =
                "Selecciona una hora"
            return
        }

        if (ejercicios.isEmpty()) {

            Toast.makeText(
                this,
                "Agrega al menos un ejercicio",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val datosRutina =
            obtenerDatosRutina(
                nombreRutina
            )

        val entrenamiento =
            EntrenamientoProgramado(
                rutinaId =
                    datosRutina.id,
                nombreRutina =
                    nombreRutina,
                descripcion =
                    datosRutina.descripcion,
                nivel =
                    datosRutina.nivel,
                fecha =
                    fecha,
                hora =
                    hora,
                duracionMinutos =
                    calcularDuracionEstimada(),
                cantidadEjercicios =
                    ejercicios.size,
                objetivo =
                    objetivo,
                ejercicios =
                    ejercicios.toList()
            )

        CalendarioRepository(this)
            .guardarEntrenamiento(
                entrenamiento
            )

        Toast.makeText(
            this,
            "Entrenamiento programado correctamente",
            Toast.LENGTH_SHORT
        ).show()

        setResult(
            RESULT_OK
        )

        finish()
    }

    private fun obtenerDatosRutina(
        nombre: String
    ): DatosRutina {

        val rutina =
            RutinasCatalog.buscarPorNombre(nombre)
                ?: return DatosRutina(
                    id = RutinasCatalog.rutinaPersonalizada.id,
                    descripcion = RutinasCatalog.rutinaPersonalizada.descripcion,
                    nivel = RutinasCatalog.rutinaPersonalizada.nivel
                )

        return DatosRutina(
            id = rutina.id,
            descripcion = rutina.descripcion,
            nivel = rutina.nivel
        )
    }

    data class DatosRutina(
        val id: Int,
        val descripcion: String,
        val nivel: String
    )
}