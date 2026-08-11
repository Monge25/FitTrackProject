package com.example.proyecto.data.repository

import android.content.Context
import com.example.proyecto.data.model.ObjetivoSemanal
import com.example.proyecto.data.model.SesionEntrenamiento
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProgresoRepository(
    context: Context
) {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    private val gson = Gson()

    fun guardarObjetivo(
        objetivo: ObjetivoSemanal
    ) {
        val json = gson.toJson(objetivo)

        preferences.edit()
            .putString(KEY_OBJETIVO, json)
            .apply()
    }

    fun obtenerObjetivo(): ObjetivoSemanal {
        val json =
            preferences.getString(
                KEY_OBJETIVO,
                null
            )

        if (json.isNullOrBlank()) {
            return ObjetivoSemanal()
        }

        return try {
            gson.fromJson(
                json,
                ObjetivoSemanal::class.java
            )
        } catch (exception: Exception) {
            ObjetivoSemanal()
        }
    }

    fun guardarSesion(
        sesion: SesionEntrenamiento
    ) {
        val sesiones =
            obtenerSesiones().toMutableList()

        sesiones.add(
            0,
            sesion
        )

        guardarSesiones(
            sesiones
        )
    }

    fun obtenerSesiones(): List<SesionEntrenamiento> {
        val json =
            preferences.getString(
                KEY_SESIONES,
                null
            )

        if (json.isNullOrBlank()) {
            return emptyList()
        }

        return try {
            val tipo =
                object :
                    TypeToken<List<SesionEntrenamiento>>() {}.type

            gson.fromJson<List<SesionEntrenamiento>>(
                json,
                tipo
            ) ?: emptyList()

        } catch (exception: Exception) {
            emptyList()
        }
    }

    fun obtenerSesion(
        id: Long
    ): SesionEntrenamiento? {
        return obtenerSesiones()
            .firstOrNull {
                it.id == id
            }
    }

    fun eliminarSesion(
        id: Long
    ) {
        val sesiones =
            obtenerSesiones()
                .filterNot {
                    it.id == id
                }

        guardarSesiones(
            sesiones
        )
    }

    fun obtenerSesionesSemanaActual():
            List<SesionEntrenamiento> {

        val inicioSemana =
            obtenerInicioSemana()

        return obtenerSesiones()
            .filter {
                it.fechaFin >= inicioSemana
            }
    }

    fun obtenerSegundosSemana(): Int {
        return obtenerSesionesSemanaActual()
            .sumOf {
                it.duracionSegundos
            }
    }

    fun obtenerPorcentajeSemanal(): Int {
        val objetivo =
            obtenerObjetivo()

        val segundosObjetivo =
            (objetivo.horasObjetivo * 3600)
                .toInt()

        if (segundosObjetivo <= 0) {
            return 0
        }

        val porcentaje =
            (
                    obtenerSegundosSemana()
                        .toFloat() /
                            segundosObjetivo.toFloat()
                    ) * 100

        return porcentaje
            .toInt()
            .coerceIn(
                0,
                100
            )
    }

    private fun guardarSesiones(
        sesiones: List<SesionEntrenamiento>
    ) {
        val json =
            gson.toJson(sesiones)

        preferences.edit()
            .putString(
                KEY_SESIONES,
                json
            )
            .apply()
    }

    private fun obtenerInicioSemana(): Long {
        val calendario =
            java.util.Calendar.getInstance()

        calendario.set(
            java.util.Calendar.DAY_OF_WEEK,
            calendario.firstDayOfWeek
        )

        calendario.set(
            java.util.Calendar.HOUR_OF_DAY,
            0
        )

        calendario.set(
            java.util.Calendar.MINUTE,
            0
        )

        calendario.set(
            java.util.Calendar.SECOND,
            0
        )

        calendario.set(
            java.util.Calendar.MILLISECOND,
            0
        )

        return calendario.timeInMillis
    }

    companion object {
        private const val PREFS_NAME =
            "fittrack_progreso"

        private const val KEY_OBJETIVO =
            "objetivo_semanal"

        private const val KEY_SESIONES =
            "sesiones_entrenamiento"
    }
}