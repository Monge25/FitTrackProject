package com.example.proyecto.data.model

data class EjercicioApi(
    val id: Int = 0,
    val rutinaId: Int = 0,
    val nombre: String = "",
    val series: Int = 0,
    val repeticiones: Int = 0,
    val peso: Double? = null,
    val descanso: Int = 0,
    val notas: String? = null,
    val esActivo: Boolean = true,
    val fechaCreacion: String = ""
)

data class CrearEjercicioRequest(
    val nombre: String,
    val series: Int,
    val repeticiones: Int,
    val peso: Double?,
    val descanso: Int,
    val notas: String?
)