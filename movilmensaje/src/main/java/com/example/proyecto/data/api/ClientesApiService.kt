package com.example.proyecto.data.api

import com.example.proyecto.data.model.BuscarClienteResponse
import com.example.proyecto.data.model.Cliente
import com.example.proyecto.data.model.ClienteRequest
import com.example.proyecto.data.model.LoginRequest
import com.example.proyecto.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ClientesApiService {
    // Autentificación
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Obtener Clientes
    @GET("api/clientes")
    suspend fun getClientes(@Header("Authorization") token: String): Response<List<Cliente>>

    @GET("api/clientes/ByClave/{clave}")
    suspend fun buscarPorClave(
        @Header("Authorization") token: String,
        @Path("clave") clave: String
    ): Response<Cliente>

    @POST("api/clientes")
    suspend fun insertar(
        @Header("Authorization") token: String,
        @Body request: ClienteRequest
    ): Response<Cliente>

    @PUT("api/clientes/{id}")
    suspend fun actualizar(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: ClienteRequest
    ): Response<Cliente>

    @DELETE("api/clientes/{id}")
    suspend fun eliminar(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}