package com.example.proyecto.data.model

    data class Cliente(
        val id: Int = 0,
        val clave: String = "",
        val nombre: String = "",
        val edad: Int = 0,
        val fechaNacimiento: String = "",
        val fechaCreacion: String = ""
    )

    data class ClienteRequest(
        val clave: String,
        val nombre: String,
        val edad: Int,
        val fechaNacimiento: String
    )

    data class BuscarClienteResponse(
        val encontrado: Boolean,
        val cliente: Cliente?
    )