package com.example.proyecto.data.model

data class SesionEntrenamiento(
    val id: Long = System.currentTimeMillis(),
    val rutinaId: Int = 0,
    val nombreRutina: String,
    val fechaInicio: Long,
    val fechaFin: Long,
    val duracionSegundos: Int,
    val ejerciciosCompletados: Int,
    val seriesCompletadas: Int,
    val frecuenciaPromedio: Int
)