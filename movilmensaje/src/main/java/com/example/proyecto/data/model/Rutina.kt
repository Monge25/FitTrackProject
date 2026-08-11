package com.example.proyecto.data.model

data class Rutina(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val nivel: String,
    val duracion: Int,
    val ejercicios: Int,
    val ejerciciosPredeterminados: List<EjercicioProgramado> = emptyList()
)