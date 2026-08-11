package com.example.proyecto.data.model

data class EjercicioProgramado(
    val id: Long = System.currentTimeMillis(),
    val nombre: String,
    val series: Int,
    val repeticiones: Int,
    val pesoKg: Float = 0f,
    val descansoSegundos: Int,
    val notas: String = ""
)