package com.example.proyecto.wear.presentation.data.model

data class Entrenamiento(
    val nombreRutina: String = "Push Day",
    val ejercicioActual: String = "Press de banca",
    val siguienteEjercicio: String = "Press militar",
    val serieActual: Int = 1,
    val totalSeries: Int = 4,
    val repeticiones: Int = 10,
    val totalEjercicios: Int = 6,
    val duracionEstimadaMinutos: Int = 45
)