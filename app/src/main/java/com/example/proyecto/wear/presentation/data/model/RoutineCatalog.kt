package com.example.proyecto.wear.presentation.data.model

/**
 * Catálogo de rutinas disponibles para elegir desde el smartwatch.
 * Cuando el teléfono envíe rutinas reales por WearListenerService,
 * esta lista se puede reemplazar por las que lleguen de la API.
 */
object RoutineCatalog {

    val routines = listOf(
        Entrenamiento(
            nombreRutina = "Push Day",
            ejercicioActual = "Press de banca",
            siguienteEjercicio = "Press militar",
            serieActual = 1,
            totalSeries = 4,
            repeticiones = 10,
            totalEjercicios = 6,
            duracionEstimadaMinutos = 60
        ),
        Entrenamiento(
            nombreRutina = "Pull Day",
            ejercicioActual = "Remo con barra",
            siguienteEjercicio = "Dominadas",
            serieActual = 1,
            totalSeries = 4,
            repeticiones = 10,
            totalEjercicios = 5,
            duracionEstimadaMinutos = 50
        ),
        Entrenamiento(
            nombreRutina = "Leg Day",
            ejercicioActual = "Sentadilla",
            siguienteEjercicio = "Peso muerto",
            serieActual = 1,
            totalSeries = 4,
            repeticiones = 12,
            totalEjercicios = 6,
            duracionEstimadaMinutos = 55
        ),
        Entrenamiento(
            nombreRutina = "Full Body",
            ejercicioActual = "Sentadilla goblet",
            siguienteEjercicio = "Press de hombro",
            serieActual = 1,
            totalSeries = 3,
            repeticiones = 12,
            totalEjercicios = 7,
            duracionEstimadaMinutos = 45
        )
    )
}
