package com.example.proyecto.data.repository

import com.example.proyecto.data.api.RetrofitInstance
import com.example.proyecto.data.model.LoginRequest

class AuthRepository {

    suspend fun login(username: String, password: String): Result<String> {
        return try {
            val response = RetrofitInstance.api.login(LoginRequest(username, password))

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body.token)
            } else {
                Result.failure(Exception("Usuario o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}