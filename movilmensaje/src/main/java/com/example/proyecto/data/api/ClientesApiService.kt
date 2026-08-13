package com.example.proyecto.data.api

import com.example.proyecto.data.model.ActualizarRutinaRequest
import com.example.proyecto.data.model.BuscarClienteResponse
import com.example.proyecto.data.model.Cliente
import com.example.proyecto.data.model.ClienteRequest
import com.example.proyecto.data.model.CrearEjercicioRequest
import com.example.proyecto.data.model.CrearRutinaRequest
import com.example.proyecto.data.model.EjercicioApi
import com.example.proyecto.data.model.LoginRequest
import com.example.proyecto.data.model.LoginResponse
import com.example.proyecto.data.model.RegisterRequest
import com.example.proyecto.data.model.Rutina
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.PATCH

interface ClientesApiService {
    // Autentificación
    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @POST("api/Auth/register")
    suspend fun registrar(@Body request: RegisterRequest): Response<Any>

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

    // ── Rutinas ───────────────────────────────────────────────
    @GET("api/rutinas")
    suspend fun getRutinas(
        @Header("Authorization") token: String
    ): Response<List<Rutina>>

    @GET("api/rutinas/{id}")
    suspend fun getRutinaPorId(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Rutina>

    @POST("api/rutinas")
    suspend fun crearRutina(
        @Header("Authorization") token: String,
        @Body request: CrearRutinaRequest
    ): Response<Rutina>

    @PUT("api/rutinas/{id}")
    suspend fun actualizarRutina(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: ActualizarRutinaRequest
    ): Response<Rutina>

    @PATCH("api/rutinas/{id}/desactivar")
    suspend fun desactivarRutina(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Any>

    @POST("api/rutinas/{id}/ejercicios")
    suspend fun agregarEjercicio(
        @Header("Authorization") token: String,
        @Path("id") rutinaId: Int,
        @Body request: CrearEjercicioRequest
    ): Response<EjercicioApi>

    @PUT("api/rutinas/{id}/ejercicios/{ejercicioId}")
    suspend fun actualizarEjercicio(
        @Header("Authorization") token: String,
        @Path("id") rutinaId: Int,
        @Path("ejercicioId") ejercicioId: Int,
        @Body request: CrearEjercicioRequest
    ): Response<EjercicioApi>

    @PATCH("api/rutinas/{id}/ejercicios/{ejercicioId}/desactivar")
    suspend fun desactivarEjercicio(
        @Header("Authorization") token: String,
        @Path("id") rutinaId: Int,
        @Path("ejercicioId") ejercicioId: Int
    ): Response<Any>
}