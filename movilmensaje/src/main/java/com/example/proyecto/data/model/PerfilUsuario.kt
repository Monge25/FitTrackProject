package com.example.proyecto.data.model

data class PerfilUsuario(
    val nombre: String = "Alexa Gastélum",
    val rol: String = "Administrador",
    val peso: Float = 63f,
    val altura: Float = 1.62f,
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