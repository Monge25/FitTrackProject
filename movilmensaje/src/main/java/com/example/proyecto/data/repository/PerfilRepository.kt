package com.example.proyecto.data.repository

import android.content.Context
import com.example.proyecto.data.model.PerfilUsuario
import com.google.gson.Gson

class PerfilRepository(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    private val gson = Gson()

    fun obtenerPerfil(): PerfilUsuario {
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

    fun guardarPerfil(
        perfil: PerfilUsuario
    ) {
        preferences.edit()
            .putString(
                KEY_PERFIL,
                gson.toJson(perfil)
            )
            .apply()
    }

    companion object {
        private const val PREFS_NAME =
            "fittrack_perfil"

        private const val KEY_PERFIL =
            "perfil_usuario"
    }
}