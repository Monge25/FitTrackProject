package com.example.proyecto.data.model

data class PerfilUsuario(
    val nombre: String = "",
    val rol: String = "",
    val email: String = "",
    val peso: Float = 70f,
    val altura: Float = 1.70f,
    val objetivo: String = "Mantener una vida activa",
    val smartwatch: String = "FitTrack Watch",
    val smartwatchConectado: Boolean = true
) {
    fun calcularImc(): Float {
        if (altura <= 0f) {
            return 0f
        }

        return peso / (altura * altura)
    }
}