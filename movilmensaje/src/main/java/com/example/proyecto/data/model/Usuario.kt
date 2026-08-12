package com.example.proyecto.data.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val correo: String,
    val rol: String,
    val esActivo: Boolean
)

data class UsuarioRequest(
    val nombre: String,
    val correo: String,
    val password: String?,
    val rol: String,
    val esActivo: Boolean
)

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: Int = 0
)