package com.example.proyecto.data.model

data class RutinaLocal(
    val id: Int = 0,
    val nombre: String = "",
    val descripcion: String = "",
    val nivel: String = "",
    val duracion: Int = 0,
    val cantidadEjercicios: Int = 0,
    val ejerciciosPredeterminados: List<EjercicioProgramado> = emptyList()
)