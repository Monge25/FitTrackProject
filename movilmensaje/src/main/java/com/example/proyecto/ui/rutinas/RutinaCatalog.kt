package com.example.proyecto.data.mock

import com.example.proyecto.data.model.EjercicioProgramado
import com.example.proyecto.data.model.RutinaLocal

object RutinasCatalog {

    val rutinas = mutableListOf(
        RutinaLocal(
            id = 1,
            nombre = "Push Day",
            descripcion = "Pecho, hombro y tríceps",
            nivel = "Intermedio",
            duracion = 60,
            cantidadEjercicios = 3,
            ejerciciosPredeterminados = listOf(
                EjercicioProgramado(nombre = "Press de banca", series = 4, repeticiones = 10, descansoSegundos = 60),
                EjercicioProgramado(nombre = "Press militar", series = 4, repeticiones = 12, descansoSegundos = 60),
                EjercicioProgramado(nombre = "Fondos de tríceps", series = 3, repeticiones = 12, descansoSegundos = 45)
            )
        ),
        RutinaLocal(
            id = 2,
            nombre = "Pull Day",
            descripcion = "Espalda y bíceps",
            nivel = "Intermedio",
            duracion = 55,
            cantidadEjercicios = 3,
            ejerciciosPredeterminados = listOf(
                EjercicioProgramado(nombre = "Jalón al pecho", series = 4, repeticiones = 10, descansoSegundos = 60),
                EjercicioProgramado(nombre = "Remo con barra", series = 4, repeticiones = 10, descansoSegundos = 75),
                EjercicioProgramado(nombre = "Curl de bíceps", series = 3, repeticiones = 12, descansoSegundos = 45)
            )
        ),
        RutinaLocal(
            id = 3,
            nombre = "Leg Day",
            descripcion = "Pierna y glúteo",
            nivel = "Avanzado",
            duracion = 70,
            cantidadEjercicios = 3,
            ejerciciosPredeterminados = listOf(
                EjercicioProgramado(nombre = "Sentadilla", series = 4, repeticiones = 10, descansoSegundos = 90),
                EjercicioProgramado(nombre = "Prensa de pierna", series = 4, repeticiones = 12, descansoSegundos = 75),
                EjercicioProgramado(nombre = "Peso muerto rumano", series = 3, repeticiones = 10, descansoSegundos = 75)
            )
        ),
        RutinaLocal(
            id = 4,
            nombre = "Full Body",
            descripcion = "Entrenamiento de cuerpo completo",
            nivel = "Principiante",
            duracion = 45,
            cantidadEjercicios = 3,
            ejerciciosPredeterminados = listOf(
                EjercicioProgramado(nombre = "Sentadilla", series = 3, repeticiones = 12, descansoSegundos = 60),
                EjercicioProgramado(nombre = "Press de banca", series = 3, repeticiones = 10, descansoSegundos = 60),
                EjercicioProgramado(nombre = "Remo con barra", series = 3, repeticiones = 10, descansoSegundos = 60)
            )
        ),
        RutinaLocal(
            id = 5,
            nombre = "Cardio",
            descripcion = "Acondicionamiento cardiovascular",
            nivel = "Intermedio",
            duracion = 30,
            cantidadEjercicios = 0
        )
    )

    val rutinaPersonalizada = RutinaLocal(
        id = 0,
        nombre = "Personalizado",
        descripcion = "Entrenamiento personalizado",
        nivel = "Personalizado",
        duracion = 0,
        cantidadEjercicios = 0
    )

    fun buscarPorNombre(nombre: String): RutinaLocal? =
        rutinas.find { it.nombre.equals(nombre, ignoreCase = true) }

    fun buscarPorId(id: Int): RutinaLocal? =
        rutinas.find { it.id == id }

    fun agregarRutina(
        nombre: String,
        descripcion: String,
        nivel: String,
        duracion: Int,
        ejerciciosPredeterminados: List<EjercicioProgramado> = emptyList()
    ): RutinaLocal {
        val siguienteId = (rutinas.maxOfOrNull { it.id } ?: 0) + 1
        val nueva = RutinaLocal(
            id = siguienteId,
            nombre = nombre,
            descripcion = descripcion,
            nivel = nivel,
            duracion = duracion,
            cantidadEjercicios = ejerciciosPredeterminados.size,
            ejerciciosPredeterminados = ejerciciosPredeterminados
        )
        rutinas.add(nueva)
        return nueva
    }

    fun actualizarRutina(
        id: Int,
        nombre: String,
        descripcion: String,
        nivel: String,
        duracion: Int,
        ejerciciosPredeterminados: List<EjercicioProgramado> = emptyList()
    ): RutinaLocal? {
        val indice = rutinas.indexOfFirst { it.id == id }
        if (indice < 0) return null
        val actualizada = RutinaLocal(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            nivel = nivel,
            duracion = duracion,
            cantidadEjercicios = ejerciciosPredeterminados.size,
            ejerciciosPredeterminados = ejerciciosPredeterminados
        )
        rutinas[indice] = actualizada
        return actualizada
    }
}