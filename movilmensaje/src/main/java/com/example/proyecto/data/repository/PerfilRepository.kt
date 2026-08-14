package com.example.proyecto.data.repository

import android.content.Context
import com.example.proyecto.data.model.PerfilUsuario
import com.example.proyecto.utils.TokenManager
import com.google.gson.Gson
import java.util.Locale

class PerfilRepository(context: Context) {

    private val appContext = context.applicationContext

    private val preferences =
        appContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    private val gson = Gson()
    private val tokenManager = TokenManager(appContext)

    /**
     * Combina los datos reales de la sesión (nombre, rol y correo,
     * los que vienen del login contra el backend) con los datos
     * físicos que el usuario captura localmente (peso, altura,
     * objetivo, smartwatch), ya que el backend todavía no expone
     * esa información en el perfil de usuario.
     */
    suspend fun obtenerPerfil(): PerfilUsuario {
        val datosFisicos = obtenerDatosFisicosLocales()

        return datosFisicos.copy(
            nombre = tokenManager.obtenerNombre(),
            rol = formatearRol(tokenManager.obtenerRol()),
            email = tokenManager.obtenerEmail()
        )
    }

    fun guardarDatosFisicos(
        peso: Float,
        altura: Float,
        objetivo: String
    ) {
        val actual = obtenerDatosFisicosLocales()

        preferences.edit()
            .putString(
                KEY_PERFIL,
                gson.toJson(
                    actual.copy(
                        peso = peso,
                        altura = altura,
                        objetivo = objetivo
                    )
                )
            )
            .apply()
    }

    private fun obtenerDatosFisicosLocales(): PerfilUsuario {
        val json =
            preferences.getString(KEY_PERFIL, null)

        if (json.isNullOrBlank()) {
            return PerfilUsuario()
        }

        return try {
            gson.fromJson(
                json,
                PerfilUsuario::class.java
            )
        } catch (exception: Exception) {
            PerfilUsuario()
        }
    }

    private fun formatearRol(rol: String): String {
        return when (rol.trim().uppercase(Locale.getDefault())) {
            "ADMINISTRATOR", "ADMINISTRADOR" -> "Administrador"
            "OPERATOR", "OPERADOR" -> "Operador"
            "" -> "Usuario"
            else -> rol.lowercase(Locale.getDefault())
                .replaceFirstChar { it.uppercase(Locale.getDefault()) }
        }
    }

    companion object {
        private const val PREFS_NAME =
            "fittrack_perfil"

        private const val KEY_PERFIL =
            "perfil_usuario"
    }
}
