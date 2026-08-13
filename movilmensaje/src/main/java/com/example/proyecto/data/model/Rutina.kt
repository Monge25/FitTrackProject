package com.example.proyecto.data.model

data class Rutina(
    val id: Int = 0,
    val nombre: String = "",
    val nivel: Int = 0,
    val objetivo: Int = 0,
    val esActivo: Boolean = true,
    val fechaCreacion: String = "",
    val ejercicios: List<EjercicioApi> = emptyList()
)

data class CrearRutinaRequest(
    val nombre: String,
    val nivel: Int,
    val objetivo: Int,
    val ejercicios: List<CrearEjercicioRequest>
)

data class ActualizarRutinaRequest(
    val nombre: String,
    val nivel: Int,
    val objetivo: Int
)