package com.example.proyecto.wear.presentation.data.model

data class HistorialEntrenamiento(
    val nombreRutina: String,
    val duracion: String,
    val ejercicios: Int,
    val frecuenciaPromedio: Int,
    val calorias: Int
)