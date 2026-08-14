package com.example.proyecto.data.model

data class SesionRequest(
    val usuarioId: Int,
    val rutinaId: Int,
    val fechaProgramada: String,
    val horaProgramada: String
)

data class EjercicioCompletadoResponse(
    val id: Int = 0,
    val ejercicioId: Int = 0,
    val nombreEjercicio: String = "",
    val seriesProgramadas: Int = 0,
    val repeticionesProgramadas: Int = 0,
    val completado: Boolean = false,
    val seriesCompletadas: Int? = null,
    val repeticionesCompletadas: Int? = null,
    val pesoUsado: Double? = null,
    val notas: String? = null,
    val fechaCompletado: String? = null
)

data class SesionResponse(
    val id: Int = 0,
    val usuarioId: Int = 0,
    val rutinaId: Int = 0,
    val nombreRutina: String = "",
    val fechaProgramada: String = "",
    val horaProgramada: String = "",
    val estado: Int = 0,
    val estadoTexto: String = "",
    val porcentajeCompletado: Int = 0,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val ejercicios: List<EjercicioCompletadoResponse> = emptyList()
)

data class CompletarEjercicioRequest(
    val seriesCompletadas: Int?,
    val repeticionesCompletadas: Int?,
    val pesoUsado: Double?,
    val notas: String?
)