package com.example.proyecto.data.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val nombreCompleto: String,
    val username: String
)