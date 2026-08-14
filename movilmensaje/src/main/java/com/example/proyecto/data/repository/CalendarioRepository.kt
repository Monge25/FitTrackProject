package com.example.proyecto.data.repository

import android.content.Context
import com.example.proyecto.data.model.EntrenamientoProgramado
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CalendarioRepository(
    context: Context
) {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    private val gson = Gson()

    fun obtenerEntrenamientos():
            List<EntrenamientoProgramado> {

        val json =
            preferences.getString(
                KEY_ENTRENAMIENTOS,
                null
            )

        if (json.isNullOrBlank()) {
            return emptyList()
        }

        return try {

            val type =
                object :
                    TypeToken<List<EntrenamientoProgramado>>() {}.type

            gson.fromJson<List<EntrenamientoProgramado>>(
                json,
                type
            ) ?: emptyList()

        } catch (_: Exception) {

            emptyList()
        }
    }

    fun guardarEntrenamiento(
        entrenamiento: EntrenamientoProgramado
    ) {

        val entrenamientos =
            obtenerEntrenamientos()
                .toMutableList()

        entrenamientos.add(
            entrenamiento
        )

        guardarLista(
            entrenamientos
        )
    }

    fun cambiarEstado(
        id: Long
    ) {

        val entrenamientos =
            obtenerEntrenamientos()
                .map { entrenamiento ->

                    if (entrenamiento.id == id) {

                        entrenamiento.copy(
                            completado =
                                !entrenamiento.completado
                        )

                    } else {

                        entrenamiento
                    }
                }

        guardarLista(
            entrenamientos
        )
    }

    fun eliminarEntrenamiento(
        id: Long
    ) {

        val entrenamientos =
            obtenerEntrenamientos()
                .filterNot {
                    it.id == id
                }

        guardarLista(
            entrenamientos
        )
    }

    private fun guardarLista(
        entrenamientos:
        List<EntrenamientoProgramado>
    ) {

        preferences
            .edit()
            .putString(
                KEY_ENTRENAMIENTOS,
                gson.toJson(entrenamientos)
            )
            .apply()
    }

    companion object {

        private const val PREFS_NAME =
            "fittrack_calendario"

        private const val KEY_ENTRENAMIENTOS =
            "entrenamientos_programados"
    }
}