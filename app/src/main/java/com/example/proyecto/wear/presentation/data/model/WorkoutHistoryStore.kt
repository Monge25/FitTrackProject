package com.example.proyecto.wear.presentation.data.model

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

// Guarda el historial de entrenamientos completados directamente en el
// reloj usando SharedPreferences. El reloj corre en modo standalone y
// no tiene base de datos propia, así que esto sirve como persistencia
// simple mientras no exista sincronización real con el historial del
// teléfono. Guarda como máximo las últimas MAX_SESIONES, la más
// reciente primero.
object WorkoutHistoryStore {

    private const val PREFS_NAME = "fittrack_historial"
    private const val KEY_SESIONES = "sesiones"
    private const val MAX_SESIONES = 10

    fun guardarSesion(
        context: Context,
        sesion: HistorialEntrenamiento
    ) {
        val prefs = prefs(context)
        val actuales = leerJsonArray(prefs)

        val nuevo = JSONObject().apply {
            put("nombreRutina", sesion.nombreRutina)
            put("duracion", sesion.duracion)
            put("ejercicios", sesion.ejercicios)
            put("frecuenciaPromedio", sesion.frecuenciaPromedio)
            put("calorias", sesion.calorias)
        }

        val resultado = JSONArray()
        resultado.put(nuevo)
        for (indice in 0 until actuales.length()) {
            if (resultado.length() >= MAX_SESIONES) break
            resultado.put(actuales.getJSONObject(indice))
        }

        prefs.edit()
            .putString(KEY_SESIONES, resultado.toString())
            .apply()
    }

    fun obtenerHistorial(context: Context): List<HistorialEntrenamiento> {
        val array = leerJsonArray(prefs(context))

        return (0 until array.length()).map { indice ->
            val objeto = array.getJSONObject(indice)
            HistorialEntrenamiento(
                nombreRutina = objeto.getString("nombreRutina"),
                duracion = objeto.getString("duracion"),
                ejercicios = objeto.getInt("ejercicios"),
                frecuenciaPromedio = objeto.getInt("frecuenciaPromedio"),
                calorias = objeto.getInt("calorias")
            )
        }
    }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun leerJsonArray(prefs: SharedPreferences): JSONArray {
        val guardado = prefs.getString(KEY_SESIONES, null) ?: return JSONArray()
        return try {
            JSONArray(guardado)
        } catch (error: Exception) {
            JSONArray()
        }
    }
}