package com.example.proyecto.data.repository

import com.example.proyecto.data.api.RetrofitInstance
import com.example.proyecto.data.model.ActualizarRutinaRequest
import com.example.proyecto.data.model.CrearEjercicioRequest
import com.example.proyecto.data.model.CrearRutinaRequest
import com.example.proyecto.data.model.EjercicioApi
import com.example.proyecto.data.model.Rutina

class RutinasRepository {

    suspend fun obtenerTodas(token: String): Result<List<Rutina>> {
        return try {
            val response = RetrofitInstance.api.getRutinas(token)
            if (response.isSuccessful && response.body() != null)
                Result.success(response.body()!!)
            else
                Result.failure(Exception("No se pudo cargar las rutinas"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun obtenerPorId(token: String, id: Int): Result<Rutina> {
        return try {
            val response = RetrofitInstance.api.getRutinaPorId(token, id)
            if (response.isSuccessful && response.body() != null)
                Result.success(response.body()!!)
            else
                Result.failure(Exception("Rutina no encontrada"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun crear(token: String, request: CrearRutinaRequest): Result<Rutina> {
        return try {
            val response = RetrofitInstance.api.crearRutina(token, request)
            if (response.isSuccessful && response.body() != null)
                Result.success(response.body()!!)
            else
                Result.failure(Exception("No se pudo guardar la rutina"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun actualizar(token: String, id: Int, request: ActualizarRutinaRequest): Result<Rutina> {
        return try {
            val response = RetrofitInstance.api.actualizarRutina(token, id, request)
            if (response.isSuccessful && response.body() != null)
                Result.success(response.body()!!)
            else
                Result.failure(Exception("No se pudo actualizar la rutina"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun desactivar(token: String, id: Int): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.desactivarRutina(token, id)
            if (response.isSuccessful)
                Result.success(Unit)
            else
                Result.failure(Exception("No se pudo desactivar la rutina"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun activar(token: String, id: Int): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.activarRutina(token, id)
            if (response.isSuccessful)
                Result.success(Unit)
            else
                Result.failure(Exception("No se pudo activar la rutina"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun agregarEjercicio(
        token: String, rutinaId: Int, request: CrearEjercicioRequest
    ): Result<EjercicioApi> {
        return try {
            val response = RetrofitInstance.api.agregarEjercicio(token, rutinaId, request)
            if (response.isSuccessful && response.body() != null)
                Result.success(response.body()!!)
            else
                Result.failure(Exception("No se pudo agregar el ejercicio"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun actualizarEjercicio(
        token: String, rutinaId: Int, ejercicioId: Int, request: CrearEjercicioRequest
    ): Result<EjercicioApi> {
        return try {
            val response = RetrofitInstance.api.actualizarEjercicio(token, rutinaId, ejercicioId, request)
            if (response.isSuccessful && response.body() != null)
                Result.success(response.body()!!)
            else
                Result.failure(Exception("No se pudo actualizar el ejercicio"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun desactivarEjercicio(
        token: String, rutinaId: Int, ejercicioId: Int
    ): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.desactivarEjercicio(token, rutinaId, ejercicioId)
            if (response.isSuccessful)
                Result.success(Unit)
            else
                Result.failure(Exception("No se pudo desactivar el ejercicio"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}