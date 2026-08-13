package com.example.proyecto.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://kotlinapi-production.up.railway.app/"
    private const val LOCAL_BASE_URL = "https://fqtr3nbs-8080.usw3.devtunnels.ms/"

    val api: ClientesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(LOCAL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClientesApiService::class.java)
    }
}