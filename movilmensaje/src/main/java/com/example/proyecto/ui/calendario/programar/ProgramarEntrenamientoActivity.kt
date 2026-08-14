package com.example.proyecto.ui.calendario.programar
import com.example.movilmensaje.R

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyecto.data.model.EjercicioApi
import com.example.proyecto.data.model.EjercicioProgramado
import com.example.proyecto.data.model.EntrenamientoProgramado
import com.example.proyecto.data.model.Rutina
import com.example.proyecto.data.repository.CalendarioRepository
import com.example.proyecto.data.repository.RutinasRepository
import com.example.proyecto.utils.TokenManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProgramarEntrenamientoActivity : AppCompatActivity() {

    private lateinit var actvRutina: AutoCompleteTextView
    private lateinit var etFecha: TextInputEditText
    private lateinit var etHora: TextInputEditText

    private lateinit var tvResumenCantidad: TextView
    private lateinit var tvResumenSeries: TextView
    private lateinit var tvResumenDuracion: TextView

    private lateinit var btnGuardar: MaterialButton
    private lateinit var btnCancelar: MaterialButton

    private lateinit var tokenManager: TokenManager
    private val rutinasRepository = RutinasRepository()

    // Rutinas reales cargadas desde la API (ver cargarRutinasDesdeApi()).
    private var rutinasDisponibles: List<Rutina> = emptyList()

    // La rutina que el usuario efectivamente eligió del dropdown (no
    // solo lo que escribió) — es la única fuente de los ejercicios y
    // del objetivo, ya no se piden por separado en esta pantalla.
    private var rutinaSeleccionada: Rutina? = null

    private val calendarioSeleccionado = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_programar_entrenamiento)

        tokenManager = TokenManager(this)

        enlazarVistas()
        configurarEventos()
        configurarFechaInicial()
        actualizarResumen(null)
        cargarRutinasDesdeApi()
    }

    private fun enlazarVistas() {
        actvRutina = findViewById(R.id.actvRutinaProgramada)
        etFecha = findViewById(R.id.etFechaProgramada)
        etHora = findViewById(R.id.etHoraProgramada)

        tvResumenCantidad = findViewById(R.id.tvResumenCantidadEjercicios)
        tvResumenSeries = findViewById(R.id.tvResumenSeries)
        tvResumenDuracion = findViewById(R.id.tvResumenDuracion)

        btnGuardar = findViewById(R.id.btnGuardarEntrenamientoProgramado)
        btnCancelar = findViewById(R.id.btnCancelarEntrenamientoProgramado)
    }

    /**
     * Carga las rutinas reales (activas) desde la base de datos.
     *
     * Ojo: el "hint" (la etiqueta "Rutina") ya está declarado en el
     * XML, en el TextInputLayout que envuelve a este campo — no se
     * debe tocar actvRutina.hint desde código, porque eso pisa el
     * hint del TextInputLayout y la etiqueta deja de verse. Mientras
     * carga, solo se deshabilita el campo (isEnabled = false).
     */
    private fun cargarRutinasDesdeApi() {

        actvRutina.isEnabled = false

        lifecycleScope.launch {

            val token = tokenManager.obtenerBearer()

            rutinasRepository.obtenerTodas(token).fold(
                onSuccess = { rutinas ->

                    rutinasDisponibles = rutinas.filter { it.esActivo }

                    actvRutina.isEnabled = true

                    actvRutina.setAdapter(
                        ArrayAdapter(
                            this@ProgramarEntrenamientoActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            rutinasDisponibles.map { it.nombre }
                        )
                    )

                    actvRutina.setOnItemClickListener { _, _, posicion, _ ->
                        seleccionarRutina(rutinasDisponibles[posicion])
                    }

                    actvRutina.setOnClickListener { actvRutina.showDropDown() }
                },
                onFailure = {
                    actvRutina.isEnabled = true
                    Toast.makeText(
                        this@ProgramarEntrenamientoActivity,
                        "No se pudieron cargar las rutinas",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    /**
     * Al elegir una rutina del dropdown, se pide su detalle completo
     * a la API (obtenerPorId) para asegurar que traiga sus ejercicios
     * reales — la lista general puede no incluirlos, igual que en
     * NuevaRutinaActivity al editar.
     */
    private fun seleccionarRutina(rutina: Rutina) {

        rutinaSeleccionada = rutina
        actualizarResumen(rutina)

        lifecycleScope.launch {
            val token = tokenManager.obtenerBearer()

            rutinasRepository.obtenerPorId(token, rutina.id).fold(
                onSuccess = { rutinaCompleta ->
                    rutinaSeleccionada = rutinaCompleta
                    actualizarResumen(rutinaCompleta)
                },
                onFailure = {
                    Toast.makeText(
                        this@ProgramarEntrenamientoActivity,
                        "No se pudieron cargar los ejercicios de la rutina",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun configurarEventos() {

        etFecha.setOnClickListener { mostrarSelectorFecha() }
        etHora.setOnClickListener { mostrarSelectorHora() }

        btnGuardar.setOnClickListener { guardarEntrenamiento() }
        btnCancelar.setOnClickListener { finish() }
    }

    private fun configurarFechaInicial() {

        calendarioSeleccionado.add(Calendar.DAY_OF_MONTH, 1)
        actualizarTextoFecha()

        val horaActual = Calendar.getInstance()
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
                calendarioSeleccionado.set(Calendar.YEAR, year)
                calendarioSeleccionado.set(Calendar.MONTH, month)
                calendarioSeleccionado.set(Calendar.DAY_OF_MONTH, day)
                actualizarTextoFecha()
            },
            calendarioSeleccionado.get(Calendar.YEAR),
            calendarioSeleccionado.get(Calendar.MONTH),
            calendarioSeleccionado.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }.show()
    }

    private fun actualizarTextoFecha() {
        val formato = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        etFecha.setText(formato.format(calendarioSeleccionado.time))
    }

    private fun mostrarSelectorHora() {
        TimePickerDialog(
            this,
            { _, hora, minuto ->
                etHora.setText(String.format(Locale.getDefault(), "%02d:%02d", hora, minuto))
            },
            calendarioSeleccionado.get(Calendar.HOUR_OF_DAY),
            calendarioSeleccionado.get(Calendar.MINUTE),
            true
        ).show()
    }

    /** Recalcula el resumen (ejercicios / series / duración) a partir de la rutina elegida. */
    private fun actualizarResumen(rutina: Rutina?) {

        val ejercicios = rutina?.ejercicios ?: emptyList()

        val totalSeries = ejercicios.sumOf { it.series }
        val duracion = calcularDuracionEstimada(ejercicios)

        tvResumenCantidad.text = ejercicios.size.toString()
        tvResumenSeries.text = totalSeries.toString()
        tvResumenDuracion.text = "$duracion min"
    }

    private fun calcularDuracionEstimada(ejercicios: List<EjercicioApi>): Int {

        if (ejercicios.isEmpty()) return 0

        val segundosTotales = ejercicios.sumOf { ejercicio ->
            val tiempoPorSerie = ejercicio.repeticiones * 4
            val tiempoActivo = ejercicio.series * tiempoPorSerie
            val descansos =
                (ejercicio.series - 1).coerceAtLeast(0) * ejercicio.descanso
            tiempoActivo + descansos
        }

        // Tiempo extra para cambiar de ejercicio.
        val transiciones = (ejercicios.size - 1).coerceAtLeast(0) * 60

        return ((segundosTotales + transiciones) / 60f)
            .toInt()
            .coerceAtLeast(1)
    }

    private fun nivelTexto(nivel: Int) = when (nivel) {
        0 -> "Principiante"
        1 -> "Intermedio"
        else -> "Avanzado"
    }

    /** Mismo orden que usa NuevaRutinaActivity para el campo Objetivo de la rutina. */
    private fun objetivoTexto(objetivo: Int) = when (objetivo) {
        0 -> "Hipertrofia"
        1 -> "Fuerza"
        2 -> "Resistencia"
        3 -> "Cardio"
        else -> "Movilidad"
    }

    private fun guardarEntrenamiento() {

        val fecha = etFecha.text?.toString()?.trim().orEmpty()
        val hora = etHora.text?.toString()?.trim().orEmpty()

        val rutina = rutinaSeleccionada

        if (rutina == null) {
            actvRutina.error = "Selecciona una rutina de la lista"
            return
        }

        if (fecha.isBlank()) {
            etFecha.error = "Selecciona una fecha"
            return
        }

        if (hora.isBlank()) {
            etHora.error = "Selecciona una hora"
            return
        }

        val ejerciciosProgramados = rutina.ejercicios.map { ej ->
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

        val entrenamiento = EntrenamientoProgramado(
            rutinaId = rutina.id,
            nombreRutina = rutina.nombre,
            nivel = nivelTexto(rutina.nivel),
            fecha = fecha,
            hora = hora,
            duracionMinutos = calcularDuracionEstimada(rutina.ejercicios),
            cantidadEjercicios = rutina.ejercicios.size,
            objetivo = objetivoTexto(rutina.objetivo),
            ejercicios = ejerciciosProgramados
        )

        CalendarioRepository(this).guardarEntrenamiento(entrenamiento)

        Toast.makeText(
            this,
            "Entrenamiento programado correctamente",
            Toast.LENGTH_SHORT
        ).show()

        setResult(RESULT_OK)
        finish()
    }
}