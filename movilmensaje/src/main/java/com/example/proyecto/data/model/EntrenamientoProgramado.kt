package com.example.proyecto.data.model

data class EntrenamientoProgramado(
    val id: Long = System.currentTimeMillis(),
    val rutinaId: Int = 0,
    val nombreRutina: String,
    val descripcion: String = "Entrenamiento programado en FitTrack",
    val nivel: String = "Intermedio",

    val fecha: String,
    val hora: String,

    val duracionMinutos: Int,
    val cantidadEjercicios: Int,
    val objetivo: String = "Hipertrofia",
    val ejercicios: List<EjercicioProgramado> = emptyList(),
    val completado: Boolean = false
)