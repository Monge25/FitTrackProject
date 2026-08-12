package com.example.proyecto.data.repository

import com.example.proyecto.data.api.RetrofitInstance
import com.example.proyecto.data.model.LoginRequest
import com.example.proyecto.data.model.LoginResponse
import com.example.proyecto.data.model.RegisterRequest

class AuthRepository {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = RetrofitInstance.api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Correo o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun registrar(name: String, email: String, password: String, role: String): Result<Unit> {
        return try {
            val rolNumero = if (role.uppercase() == "ADMINISTRATOR") 0 else 1
            val response = RetrofitInstance.api.registrar(
                RegisterRequest(
                    nombre = name,
                    email = email,
                    password = password,
                    rol = rolNumero
                )
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else if (response.code() == 409) {
                Result.failure(Exception("Ya existe un usuario con ese correo"))
            } else {
                Result.failure(Exception("No se pudo crear el usuario"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}