package com.example.proyecto.wear.presentation.data.model

/** Un ejercicio real de una rutina, tal como lo mandó el teléfono. */
data class EjercicioRemoto(
    val nombre: String,
    val series: Int,
    val repeticiones: Int,
    val descansoSegundos: Int
)

/** Una rutina real (de la API), con todos sus ejercicios, mandada por el teléfono. */
data class RutinaRemota(
    val id: Int,
    val nombre: String,
    val nivel: Int,
    val ejercicios: List<EjercicioRemoto>
)

/**
 * Interpreta el mensaje que manda PhoneWearListenerService.kt en
 * respuesta a PATH_REQUEST_ROUTINES.
 *
 * Formato (una rutina por línea):
 *   id|nombre|nivel|ej1Nombre,ej1Series,ej1Reps,ej1Descanso;ej2...
 */
object RutinasRemotasParser {

    fun parsear(mensaje: String): List<RutinaRemota> {
        if (mensaje.isBlank()) return emptyList()

        return mensaje
            .split("\n")
            .mapNotNull { linea -> parsearLinea(linea) }
    }

    private fun parsearLinea(linea: String): RutinaRemota? {
        if (linea.isBlank()) return null

        val partes = linea.split("|", limit = 4)
        if (partes.size < 4) return null

        val id = partes[0].toIntOrNull() ?: return null
        val nombre = partes[1]
        val nivel = partes[2].toIntOrNull() ?: 0
        val ejerciciosBlob = partes[3]

        val ejercicios = ejerciciosBlob
            .split(";")
            .filter { it.isNotBlank() }
            .mapNotNull { parsearEjercicio(it) }

        return RutinaRemota(
            id = id,
            nombre = nombre,
            nivel = nivel,
            ejercicios = ejercicios
        )
    }

    private fun parsearEjercicio(texto: String): EjercicioRemoto? {
        val campos = texto.split(",")
        if (campos.size < 4) return null

        return EjercicioRemoto(
            nombre = campos[0],
            series = campos[1].toIntOrNull() ?: 1,
            repeticiones = campos[2].toIntOrNull() ?: 0,
            descansoSegundos = campos[3].toIntOrNull() ?: 45
        )
    }
}