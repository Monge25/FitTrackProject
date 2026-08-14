package com.example.proyecto.data.model

data class LoginRequest(
    val email   : String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val usuarioId: Int,
    val nombre: String,
    val email: String,
    val rol: String
)