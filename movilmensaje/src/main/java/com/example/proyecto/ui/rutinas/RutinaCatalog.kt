package com.example.proyecto.data.mock

import com.example.proyecto.data.model.EjercicioProgramado
import com.example.proyecto.data.model.Rutina

/**
 * Fuente única de las rutinas de ejemplo, incluyendo sus ejercicios
 * predeterminados. La usan RutinasFragment (para listarlas),
 * DashboardFragment (para el total real) y
 * ProgramarEntrenamientoActivity (para precargar ejercicios al
 * elegir una rutina).
 *
 * TODO: reemplazar por una llamada a la API cuando exista un
 * endpoint de rutinas en el backend.
 */
object RutinasCatalog {

    val rutinas = mutableListOf(

        Rutina(
            id = 1,
            nombre = "Push Day",
            descripcion = "Pecho, hombro y tríceps",
            nivel = "Intermedio",
            duracion = 60,
            ejercicios = 3,
            ejerciciosPredeterminados = listOf(
                EjercicioProgramado(
                    nombre = "Press de banca",
                    series = 4,
                    repeticiones = 10,
                    descansoSegundos = 60
                ),
                EjercicioProgramado(
                    nombre = "Press militar",
                    series = 4,
                    repeticiones = 12,
                    descansoSegundos = 60
                ),
                EjercicioProgramado(
                    nombre = "Fondos de tríceps",
                    series = 3,
                    repeticiones = 12,
                    descansoSegundos = 45
                )
            )
        ),

        Rutina(
            id = 2,
            nombre = "Pull Day",
            descripcion = "Espalda y bíceps",
            nivel = "Intermedio",
            duracion = 55,
            ejercicios = 3,
            ejerciciosPredeterminados = listOf(
                EjercicioProgramado(
                    nombre = "Jalón al pecho",
                    series = 4,
                    repeticiones = 10,
                    descansoSegundos = 60
                ),
                EjercicioProgramado(
                    nombre = "Remo con barra",
                    series = 4,
                    repeticiones = 10,
                    descansoSegundos = 75
                ),
                EjercicioProgramado(
                    nombre = "Curl de bíceps",
                    series = 3,
                    repeticiones = 12,
                    descansoSegundos = 45
                )
            )
        ),

        Rutina(
            id = 3,
            nombre = "Leg Day",
            descripcion = "Pierna y glúteo",
            nivel = "Avanzado",
            duracion = 70,
            ejercicios = 3,
            ejerciciosPredeterminados = listOf(
                EjercicioProgramado(
                    nombre = "Sentadilla",
                    series = 4,
                    repeticiones = 10,
                    descansoSegundos = 90
                ),
                EjercicioProgramado(
                    nombre = "Prensa de pierna",
                    series = 4,
                    repeticiones = 12,
                    descansoSegundos = 75
                ),
                EjercicioProgramado(
                    nombre = "Peso muerto rumano",
                    series = 3,
                    repeticiones = 10,
                    descansoSegundos = 75
                )
            )
        ),

        Rutina(
            id = 4,
            nombre = "Full Body",
            descripcion = "Entrenamiento de cuerpo completo",
            nivel = "Principiante",
            duracion = 45,
            ejercicios = 3,
            ejerciciosPredeterminados = listOf(
                EjercicioProgramado(
                    nombre = "Sentadilla",
                    series = 3,
                    repeticiones = 12,
                    descansoSegundos = 60
                ),
                EjercicioProgramado(
                    nombre = "Press de banca",
                    series = 3,
                    repeticiones = 10,
                    descansoSegundos = 60
                ),
                EjercicioProgramado(
                    nombre = "Remo con barra",
                    series = 3,
                    repeticiones = 10,
                    descansoSegundos = 60
                )
            )
        ),

        Rutina(
            id = 5,
            nombre = "Cardio",
            descripcion = "Acondicionamiento cardiovascular",
            nivel = "Intermedio",
            duracion = 30,
            ejercicios = 0
        )
    )

    /** Datos con los que se guarda un entrenamiento "Personalizado", que no está en el catálogo. */
    val rutinaPersonalizada = Rutina(
        id = 0,
        nombre = "Personalizado",
        descripcion = "Entrenamiento personalizado",
        nivel = "Personalizado",
        duracion = 0,
        ejercicios = 0
    )

    fun buscarPorNombre(nombre: String): Rutina? {
        return rutinas.find {
            it.nombre.equals(nombre, ignoreCase = true)
        }
    }

    /** Da de alta una rutina nueva y le asigna el siguiente id disponible. */
    fun agregarRutina(
        nombre: String,
        descripcion: String,
        nivel: String,
        duracion: Int,
        ejerciciosPredeterminados: List<EjercicioProgramado> = emptyList()
    ): Rutina {

        val siguienteId =
            (rutinas.maxOfOrNull { it.id } ?: 0) + 1

        val nuevaRutina = Rutina(
            id = siguienteId,
            nombre = nombre,
            descripcion = descripcion,
            nivel = nivel,
            duracion = duracion,
            ejercicios = ejerciciosPredeterminados.size,
            ejerciciosPredeterminados = ejerciciosPredeterminados
        )

        rutinas.add(nuevaRutina)

        return nuevaRutina
    }

    fun buscarPorId(id: Int): Rutina? {
        return rutinas.find { it.id == id }
    }

    /** Reemplaza una rutina existente (conservando su id) con los datos editados. */
    fun actualizarRutina(
        id: Int,
        nombre: String,
        descripcion: String,
        nivel: String,
        duracion: Int,
        ejerciciosPredeterminados: List<EjercicioProgramado> = emptyList()
    ): Rutina? {

        val indice =
            rutinas.indexOfFirst { it.id == id }

        if (indice < 0) {
            return null
        }

        val rutinaActualizada = Rutina(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            nivel = nivel,
            duracion = duracion,
            ejercicios = ejerciciosPredeterminados.size,
            ejerciciosPredeterminados = ejerciciosPredeterminados
        )

        rutinas[indice] = rutinaActualizada

        return rutinaActualizada
    }
}