package com.example.proyecto.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(
    name = "fittrack_session"
)

class TokenManager(
    private val context: Context
) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val NOMBRE_KEY = stringPreferencesKey("nombre")
        private val ROL_KEY = stringPreferencesKey("rol")
    }

    suspend fun guardarToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun obtenerToken(): String? {
        val preferences = context.dataStore.data.first()

        return preferences[TOKEN_KEY]
    }

    suspend fun obtenerBearer(): String {
        val token = obtenerToken()

        return if (token.isNullOrBlank()) {
            ""
        } else {
            "Bearer $token"
        }
    }

    suspend fun guardarDatosUsuario(
        nombre: String,
        rol: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[NOMBRE_KEY] = nombre
            preferences[ROL_KEY] = rol
        }
    }

    suspend fun obtenerNombre(): String {
        val preferences = context.dataStore.data.first()

        return preferences[NOMBRE_KEY] ?: "Usuario"
    }

    suspend fun obtenerRol(): String {
        val preferences = context.dataStore.data.first()

        return preferences[ROL_KEY] ?: "OPERADOR"
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}