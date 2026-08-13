package com.example.proyecto.data.model

data class Usuario(
    val id: Int = 0,
    val nombre: String = "",
    val correo: String = "",
    val rol: Int = 1,
    val esActivo: Boolean = true
)

data class ActualizarUsuarioRequest(
    val nombre: String,
    val email: String,
    val rol: Int,
    val esActivo: Boolean,
    val password: String? = null
)

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: Int = 0
)