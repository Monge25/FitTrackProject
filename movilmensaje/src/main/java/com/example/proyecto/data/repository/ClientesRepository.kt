package com.example.proyecto.data.repository

import com.example.proyecto.data.api.RetrofitInstance
import com.example.proyecto.data.model.BuscarClienteResponse
import com.example.proyecto.data.model.Cliente
import com.example.proyecto.data.model.ClienteRequest

class ClientesRepository {

    suspend fun obtenerTodos(bearerToken: String): Result<List<Cliente>> {
        return try {
            val response = RetrofitInstance.api.getClientes(bearerToken)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No se pudo cargar la lista de clientes"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun buscarPorClave(bearerToken: String, clave: String): Result<BuscarClienteResponse> {
        return try {
            val response = RetrofitInstance.api.buscarPorClave(bearerToken, clave)
            val clienteEncontrado: Cliente? = response.body()

            if (response.isSuccessful && clienteEncontrado != null) {
                Result.success(BuscarClienteResponse(true, clienteEncontrado))
            } else if (response.code() == 404) {
                Result.success(BuscarClienteResponse(false, null))
            } else {
                Result.failure(Exception("Error al buscar el cliente"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun insertar(bearerToken: String, request: ClienteRequest): Result<Cliente> {
        return try {
            val response = RetrofitInstance.api.insertar(bearerToken, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 409) {
                Result.failure(Exception("Ya existe un cliente con esa clave"))
            } else {
                Result.failure(Exception("No se pudo guardar el cliente"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun actualizar(bearerToken: String, id: Int, request: ClienteRequest): Result<Cliente> {
        return try {
            val response = RetrofitInstance.api.actualizar(bearerToken, id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 409) {
                Result.failure(Exception("Esa clave ya está en uso por otro cliente"))
            } else {
                Result.failure(Exception("No se pudo actualizar el cliente"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun eliminar(bearerToken: String, id: Int): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.eliminar(bearerToken, id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo eliminar el cliente"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}