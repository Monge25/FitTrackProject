package com.example.proyecto.utils

/**
 * Roles actuales: 0 = administrador, 1 = operador (mismo criterio que
 * Usuario.rol y RegisterRequest.rol). La sesión guarda el rol como
 * texto (TokenManager.obtenerRol()), así que aquí se normaliza para
 * que funcione sea cual sea el formato exacto que mande el backend
 * en el login ("0", "administrador", "Administrator", etc.).
 */
object Permisos {

    fun esAdmin(rolTexto: String): Boolean {
        val normalizado = rolTexto.trim()

        return normalizado == "0" ||
                normalizado.equals("admin", ignoreCase = true) ||
                normalizado.equals("administrador", ignoreCase = true) ||
                normalizado.equals("administrator", ignoreCase = true)
    }

    fun puedeVerUsuarios(rolTexto: String): Boolean = esAdmin(rolTexto)
}