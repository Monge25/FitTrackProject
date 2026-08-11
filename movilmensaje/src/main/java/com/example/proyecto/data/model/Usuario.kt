package com.example.proyecto.data.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val correo: String,
    val rol: String,
    val activo: Boolean
)

data class UsuarioRequest(
    val nombre: String,
    val correo: String,
    val password: String?,
    val rol: String,
    val activo: Boolean
)