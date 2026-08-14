package com.example.proyecto.data.repository

import com.example.proyecto.data.api.RetrofitInstance
import com.example.proyecto.data.model.CompletarEjercicioRequest
import com.example.proyecto.data.model.SesionRequest
import com.example.proyecto.data.model.SesionResponse

class SesionesRepository {

    suspend fun programar(token: String, request: SesionRequest): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.programarSesion(token, request)
            android.util.Log.d("SESION", "Código: ${response.code()}")
            android.util.Log.d("SESION", "Error: ${response.errorBody()?.string()}")
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("No se pudo programar la sesión"))
        } catch (e: Exception) {
            android.util.Log.d("SESION", "Excepción: ${e.message}")
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun obtenerPorUsuario(
        token: String,
        usuarioId: Int
    ): Result<List<SesionResponse>> {
        return try {
            val response = RetrofitInstance.api.getSesionesPorUsuario(token, usuarioId)
            if (response.isSuccessful && response.body() != null)
                Result.success(response.body()!!)
            else Result.failure(Exception("No se pudieron cargar las sesiones"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun obtenerPorId(
        token: String,
        id: Int
    ): Result<SesionResponse> {
        return try {
            val response = RetrofitInstance.api.getSesionPorId(token, id)
            if (response.isSuccessful && response.body() != null)
                Result.success(response.body()!!)
            else Result.failure(Exception("Sesión no encontrada"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun iniciar(token: String, id: Int): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.iniciarSesion(token, id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("No se pudo iniciar la sesión"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun completarEjercicio(
        token: String,
        sesionId: Int,
        ejCompletadoId: Int,
        request: CompletarEjercicioRequest
    ): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.completarEjercicio(
                token, sesionId, ejCompletadoId, request
            )
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("No se pudo completar el ejercicio"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun finalizar(token: String, id: Int): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.finalizarSesion(token, id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("No se pudo finalizar la sesión"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}