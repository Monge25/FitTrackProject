package com.example.proyecto.ui.dashboard
import com.example.movilmensaje.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyecto.Principal
import com.example.proyecto.data.mock.RutinasCatalog
import com.example.proyecto.data.mock.UsuariosCatalog
import com.example.proyecto.data.repository.CalendarioRepository
import com.example.proyecto.data.repository.ClientesRepository
import com.example.proyecto.data.repository.ProgresoRepository
import com.example.proyecto.utils.TokenManager
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {

    // INFORMACIÓN DEL USUARIO

    private lateinit var tvSaludo: TextView
    private lateinit var tvRolUsuario: TextView
    private lateinit var tvMensajeDashboard: TextView
    private lateinit var tvChipFechaHoy: TextView


    // ESTADÍSTICAS

    private lateinit var tvEntrenamientosHoy: TextView
    private lateinit var tvNumeroEntrenamientos: TextView
    private lateinit var tvTotalClientes: TextView
    private lateinit var tvTotalRutinas: TextView
    private lateinit var tvTotalEntrenadores: TextView
    private lateinit var tvProgresoPromedio: TextView


    // TARJETAS

    private lateinit var cardEntrenamientoHoy: MaterialCardView
    private lateinit var cardClientes: MaterialCardView
    private lateinit var cardRutinas: MaterialCardView
    private lateinit var cardEntrenadores: MaterialCardView
    private lateinit var cardProgreso: MaterialCardView

    private lateinit var btnAvatarPerfil: TextView

    // ACCESOS RÁPIDOS

    private lateinit var btnVerClientes: MaterialCardView
    private lateinit var btnVerRutinas: MaterialCardView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.dashboard_fragment,
            container,
            false
        )
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        inicializarComponentes(view)

        configurarInformacionUsuario()

        configurarEstadisticas()

        configurarNavegacion()
    }


    private fun inicializarComponentes(
        view: View
    ) {

        // USUARIO

        tvSaludo = view.findViewById(
            R.id.tvSaludo
        )

        tvRolUsuario = view.findViewById(
            R.id.tvRolUsuario
        )

        tvMensajeDashboard = view.findViewById(
            R.id.tvMensajeDashboard
        )

        tvChipFechaHoy = view.findViewById(
            R.id.tvChipFechaHoy
        )


        // ESTADÍSTICAS

        tvEntrenamientosHoy = view.findViewById(
            R.id.tvEntrenamientosHoy
        )

        tvNumeroEntrenamientos = view.findViewById(
            R.id.tvNumeroEntrenamientos
        )

        tvTotalClientes = view.findViewById(
            R.id.tvTotalClientes
        )

        tvTotalRutinas = view.findViewById(
            R.id.tvTotalRutinas
        )

        tvTotalEntrenadores = view.findViewById(
            R.id.tvTotalEntrenadores
        )

        tvProgresoPromedio = view.findViewById(
            R.id.tvProgresoPromedio
        )


        // TARJETAS

        cardEntrenamientoHoy = view.findViewById(
            R.id.cardEntrenamientoHoy
        )

        cardClientes = view.findViewById(
            R.id.cardClientes
        )

        cardRutinas = view.findViewById(
            R.id.cardRutinas
        )

        cardEntrenadores = view.findViewById(
            R.id.cardEntrenadores
        )

        cardProgreso = view.findViewById(
            R.id.cardProgreso
        )


        // ACCESOS RÁPIDOS

        btnVerClientes = view.findViewById(
            R.id.btnVerClientes
        )

        btnVerRutinas = view.findViewById(
            R.id.btnVerRutinas
        )

        btnAvatarPerfil =
            view.findViewById(
                R.id.btnAvatarPerfil
            )
    }


    private fun configurarInformacionUsuario() {

        val formatoChip =
            SimpleDateFormat("EEE d MMM", Locale("es", "MX"))

        tvChipFechaHoy.text =
            formatoChip.format(Date()).uppercase()

        // Mensaje mientras se carga la sesión real
        tvSaludo.text = "¡Hola!"

        tvMensajeDashboard.text =
            "El gimnasio está activo. " +
                    "Revisa el rendimiento de tus atletas " +
                    "y organiza los entrenamientos."

        val tokenManager = TokenManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {

            val nombre = tokenManager.obtenerNombre()
            val rol = tokenManager.obtenerRol()

            tvSaludo.text = "¡Hola, $nombre!"

            tvRolUsuario.text = "$rol de FitTrack"
        }
    }


    private fun configurarEstadisticas() {

        // RUTINAS Y CUENTAS: catálogos compartidos, disponibles al instante

        tvTotalRutinas.text =
            RutinasCatalog.rutinas.size.toString()

        tvTotalEntrenadores.text =
            UsuariosCatalog.usuarios
                .count { it.esActivo }
                .toString()

        // CALENDARIO: entrenamientos programados que faltan por completar

        val calendarioRepository =
            CalendarioRepository(requireContext())

        val entrenamientosPendientes =
            calendarioRepository.obtenerEntrenamientos()
                .count { !it.completado }

        tvNumeroEntrenamientos.text =
            entrenamientosPendientes.toString()

        tvEntrenamientosHoy.text =
            "Sesiones programadas"

        // PROGRESO: porcentaje semanal real ya calculado en el repositorio

        val progresoRepository =
            ProgresoRepository(requireContext())

        tvProgresoPromedio.text =
            "${progresoRepository.obtenerPorcentajeSemanal()}%"

        // CLIENTES: viene del backend, así que se pide aparte (asíncrono)

        tvTotalClientes.text = "…"

        cargarTotalClientes()
    }


    private fun cargarTotalClientes() {

        val tokenManager = TokenManager(requireContext())
        val clientesRepository = ClientesRepository()

        viewLifecycleOwner.lifecycleScope.launch {

            val bearer = tokenManager.obtenerBearer()

            if (bearer.isBlank()) {
                tvTotalClientes.text = "0"
                return@launch
            }

            clientesRepository.obtenerTodos(bearer)
                .onSuccess { clientes ->
                    tvTotalClientes.text = clientes.size.toString()
                }
                .onFailure {
                    tvTotalClientes.text = "--"
                }
        }
    }


    private fun configurarNavegacion() {

        // ACTIVIDAD DE HOY -> CALENDARIO
        // (esta tarjeta muestra "sesiones programadas", que es
        // justo lo que administra CalendarioFragment, no Rutinas)

        cardEntrenamientoHoy.setOnClickListener {

            navegarA(
                Principal.PANTALLA_CALENDARIO
            )
        }


        // ATLETAS -> USUARIOS
        // (no existe una pantalla de "clientes/atletas" en el menú
        // inferior todavía; Usuarios es la única pantalla real de
        // personas que existe hoy en la app)

        cardClientes.setOnClickListener {

            navegarA(
                Principal.PANTALLA_USUARIOS
            )
        }


        // RUTINAS ACTIVAS -> RUTINAS

        cardRutinas.setOnClickListener {

            navegarA(
                Principal.PANTALLA_RUTINAS
            )
        }


        // CUENTAS ACTIVAS -> USUARIOS

        cardEntrenadores.setOnClickListener {

            navegarA(
                Principal.PANTALLA_USUARIOS
            )
        }


        // PROGRESO -> PROGRESO

        cardProgreso.setOnClickListener {

            navegarA(
                Principal.PANTALLA_PROGRESO
            )
        }


        // MIS ATLETAS -> USUARIOS

        btnVerClientes.setOnClickListener {

            navegarA(
                Principal.PANTALLA_USUARIOS
            )
        }


        // ZONA DE ENTRENAMIENTO -> RUTINAS

        btnVerRutinas.setOnClickListener {

            navegarA(
                Principal.PANTALLA_RUTINAS
            )
        }

        btnAvatarPerfil.setOnClickListener {
            navegarA(
                Principal.PANTALLA_PERFIL
            )
        }
    }


    private fun navegarA(
        pantalla: Int
    ) {

        val principal =
            activity as? Principal

        principal?.irAPantalla(
            pantalla
        )
    }
}