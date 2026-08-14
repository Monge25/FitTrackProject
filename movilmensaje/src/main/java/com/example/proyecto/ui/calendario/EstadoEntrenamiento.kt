package com.example.proyecto.ui.calendario

import com.example.proyecto.data.model.EntrenamientoProgramado
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Estado real de un entrenamiento programado, calculado a partir de
 * si ya se marcó como completado y de si su fecha/hora ya pasó.
 */
enum class EstadoEntrenamiento {
    CUMPLIDA,
    VENCIDA,
    PROXIMA
}

/**
 * - CUMPLIDA: el usuario la marcó como completada.
 * - VENCIDA: no se completó y su fecha/hora ya pasó.
 * - PROXIMA: no se completó y su fecha/hora todavía no llega.
 *
 * El formato de fecha debe coincidir exactamente con el que usa
 * ProgramarEntrenamientoActivity al guardarla ("dd MMM yyyy").
 */
fun EntrenamientoProgramado.calcularEstado(): EstadoEntrenamiento {

    if (completado) {
        return EstadoEntrenamiento.CUMPLIDA
    }

    val fechaHora = parsearFechaHora(fecha, hora)
    val ahora = Calendar.getInstance().time

    return if (fechaHora != null && fechaHora.before(ahora)) {
        EstadoEntrenamiento.VENCIDA
    } else {
        EstadoEntrenamiento.PROXIMA
    }
}

private fun parsearFechaHora(fecha: String, hora: String): Date? {
    return try {
        val formato = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        formato.parse("$fecha $hora")
    } catch (e: Exception) {
        null
    }
}