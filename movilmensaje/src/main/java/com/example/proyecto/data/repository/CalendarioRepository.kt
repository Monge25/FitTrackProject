package com.example.proyecto.data.repository

import android.content.Context
import com.example.proyecto.data.model.EjercicioProgramado
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
            return crearEntrenamientosIniciales()
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

    private fun crearEntrenamientosIniciales():
            List<EntrenamientoProgramado> {

        val iniciales =
            listOf(

                EntrenamientoProgramado(
                    id = 1L,
                    rutinaId = 1,
                    nombreRutina = "Push Day",
                    descripcion =
                        "Pecho, hombro y tríceps",
                    nivel = "Intermedio",
                    fecha = "Lunes",
                    hora = "18:00",
                    duracionMinutos = 20,
                    cantidadEjercicios = 3,
                    objetivo = "Hipertrofia",
                    ejercicios = listOf(
                        EjercicioProgramado(
                            id = 101L,
                            nombre = "Press de banca",
                            series = 4,
                            repeticiones = 10,
                            pesoKg = 0f,
                            descansoSegundos = 60
                        ),
                        EjercicioProgramado(
                            id = 102L,
                            nombre = "Press militar",
                            series = 4,
                            repeticiones = 12,
                            pesoKg = 0f,
                            descansoSegundos = 60
                        ),
                        EjercicioProgramado(
                            id = 103L,
                            nombre = "Fondos de tríceps",
                            series = 3,
                            repeticiones = 12,
                            pesoKg = 0f,
                            descansoSegundos = 45
                        )
                    )
                ),

                EntrenamientoProgramado(
                    id = 2L,
                    rutinaId = 3,
                    nombreRutina = "Leg Day",
                    descripcion =
                        "Pierna y glúteo",
                    nivel = "Avanzado",
                    fecha = "Miércoles",
                    hora = "17:30",
                    duracionMinutos = 25,
                    cantidadEjercicios = 3,
                    objetivo = "Fuerza",
                    ejercicios = listOf(
                        EjercicioProgramado(
                            id = 201L,
                            nombre = "Sentadilla",
                            series = 4,
                            repeticiones = 10,
                            pesoKg = 0f,
                            descansoSegundos = 90
                        ),
                        EjercicioProgramado(
                            id = 202L,
                            nombre = "Prensa de pierna",
                            series = 4,
                            repeticiones = 12,
                            pesoKg = 0f,
                            descansoSegundos = 75
                        ),
                        EjercicioProgramado(
                            id = 203L,
                            nombre = "Peso muerto rumano",
                            series = 3,
                            repeticiones = 10,
                            pesoKg = 0f,
                            descansoSegundos = 75
                        )
                    )
                ),

                EntrenamientoProgramado(
                    id = 3L,
                    rutinaId = 4,
                    nombreRutina = "Full Body",
                    descripcion =
                        "Entrenamiento de cuerpo completo",
                    nivel = "Principiante",
                    fecha = "Sábado",
                    hora = "10:00",
                    duracionMinutos = 18,
                    cantidadEjercicios = 3,
                    objetivo = "Resistencia",
                    ejercicios = listOf(
                        EjercicioProgramado(
                            id = 301L,
                            nombre = "Sentadilla",
                            series = 3,
                            repeticiones = 12,
                            pesoKg = 0f,
                            descansoSegundos = 60
                        ),
                        EjercicioProgramado(
                            id = 302L,
                            nombre = "Press de banca",
                            series = 3,
                            repeticiones = 10,
                            pesoKg = 0f,
                            descansoSegundos = 60
                        ),
                        EjercicioProgramado(
                            id = 303L,
                            nombre = "Remo con barra",
                            series = 3,
                            repeticiones = 10,
                            pesoKg = 0f,
                            descansoSegundos = 60
                        )
                    )
                )
            )

        guardarLista(
            iniciales
        )

        return iniciales
    }

    companion object {

        private const val PREFS_NAME =
            "fittrack_calendario"

        private const val KEY_ENTRENAMIENTOS =
            "entrenamientos_programados"
    }
}