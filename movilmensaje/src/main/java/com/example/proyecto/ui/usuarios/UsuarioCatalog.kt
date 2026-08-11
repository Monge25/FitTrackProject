package com.example.proyecto.data.mock

import com.example.proyecto.data.model.Usuario

/**
 * Fuente única de las cuentas de ejemplo. La usa UsuariosFragment
 * (para listarlas y editarlas) y DashboardFragment (para mostrar el
 * total real de cuentas activas).
 *
 * TODO: reemplazar por una llamada a la API cuando exista un
 * endpoint de usuarios en el backend (hoy ClientesApiService solo
 * maneja clientes/login).
 */
object UsuariosCatalog {

    val usuarios = mutableListOf(

        Usuario(
            id = 1,
            nombre = "Alexa Gastélum",
            correo = "alexa@fittrack.com",
            rol = "ADMINISTRADOR",
            activo = true
        ),

        Usuario(
            id = 2,
            nombre = "Nicol",
            correo = "nicol@fittrack.com",
            rol = "OPERADOR",
            activo = true
        ),

        Usuario(
            id = 3,
            nombre = "Emiliano",
            correo = "emiliano@fittrack.com",
            rol = "OPERADOR",
            activo = true
        )
    )

    fun existeCorreo(correo: String): Boolean {
        return usuarios.any {
            it.correo.equals(correo, ignoreCase = true)
        }
    }

    /**
     * Da de alta una cuenta nueva (ej. desde RegistroActivity) y le
     * asigna el siguiente id disponible.
     *
     * OJO: esto solo la agrega a este catálogo local, que alimenta
     * la pantalla "Usuarios". NO crea una cuenta con la que se
     * pueda iniciar sesión real, porque el login pega contra el
     * backend (AuthRepository/ClientesApiService), que no tiene
     * endpoint de registro todavía.
     */
    fun registrarUsuario(
        nombre: String,
        correo: String,
        rol: String = "OPERADOR"
    ): Usuario {

        val siguienteId =
            (usuarios.maxOfOrNull { it.id } ?: 0) + 1

        val nuevoUsuario = Usuario(
            id = siguienteId,
            nombre = nombre,
            correo = correo,
            rol = rol,
            activo = true
        )

        usuarios.add(nuevoUsuario)

        return nuevoUsuario
    }
}