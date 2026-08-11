package com.example.proyecto.data.model

data class ObjetivoSemanal(
    val horasObjetivo: Float = 5f,
    val sesionesObjetivo: Int = 4,
    val diasSeleccionados: List<String> = emptyList(),
    val rutinasSeleccionadas: List<String> = emptyList()
)