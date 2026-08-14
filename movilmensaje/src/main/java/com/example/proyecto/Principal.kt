package com.example.proyecto
import com.example.movilmensaje.R

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.proyecto.utils.Permisos
import com.example.proyecto.utils.TokenManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class Principal : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView

    // Progreso está oculta para todos por ahora (sin borrar nada del
    // proyecto, solo inalcanzable desde la navegación). Usuarios
    // depende del rol — se resuelve en aplicarPermisos().
    private var puedeVerUsuarios = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_principal)

        inicializarComponentes()
        configurarViewPager()
        configurarNavegacionInferior()
        aplicarPermisos()
    }

    private fun aplicarPermisos() {

        // El swipe permite llegar a cualquier página sin pasar por
        // el menú inferior; se desactiva para que ocultar pestañas
        // del menú sea suficiente para bloquear el acceso (la
        // navegación queda solo por el menú y por irAPantalla,
        // ambos ya filtrados aquí).
        viewPager.isUserInputEnabled = false

        // Progreso: oculta para todos los roles por ahora.
        bottomNavigation.menu
            .findItem(R.id.navProgreso)
            ?.isVisible = false

        lifecycleScope.launch {

            val rol = TokenManager(this@Principal).obtenerRol()

            puedeVerUsuarios = Permisos.puedeVerUsuarios(rol)

            bottomNavigation.menu
                .findItem(R.id.navUsuarios)
                ?.isVisible = puedeVerUsuarios
        }
    }

    private fun inicializarComponentes() {
        viewPager = findViewById(R.id.viewPager)
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    private fun configurarViewPager() {

        viewPager.adapter = ViewPagerAdapter(this)

        // Ahora son 6 pantallas
        viewPager.offscreenPageLimit = 6

        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    bottomNavigation.selectedItemId = when (position) {

                        PANTALLA_INICIO -> R.id.navInicio

                        PANTALLA_RUTINAS -> R.id.navRutinas

                        PANTALLA_CALENDARIO -> R.id.navCalendario

                        PANTALLA_PROGRESO -> R.id.navProgreso

                        PANTALLA_USUARIOS -> R.id.navUsuarios

                        PANTALLA_PERFIL -> R.id.navPerfil

                        else -> R.id.navInicio
                    }
                }
            }
        )
    }

    private fun configurarNavegacionInferior() {

        bottomNavigation.setOnItemSelectedListener { item ->

            val posicion = when (item.itemId) {

                R.id.navInicio -> PANTALLA_INICIO

                R.id.navRutinas -> PANTALLA_RUTINAS

                R.id.navCalendario -> PANTALLA_CALENDARIO

                R.id.navProgreso -> PANTALLA_PROGRESO

                R.id.navUsuarios -> PANTALLA_USUARIOS

                R.id.navPerfil -> PANTALLA_PERFIL

                else -> PANTALLA_INICIO
            }

            viewPager.setCurrentItem(posicion, false)

            true
        }
    }

    fun irAPantalla(posicion: Int) {

        // Progreso: bloqueada para todos por ahora.
        if (posicion == PANTALLA_PROGRESO) {
            return
        }

        // Usuarios: solo ADMIN.
        if (posicion == PANTALLA_USUARIOS && !puedeVerUsuarios) {
            return
        }

        if (posicion in 0 until TOTAL_PANTALLAS) {

            viewPager.setCurrentItem(posicion, true)

        }
    }

    companion object {

        const val PANTALLA_INICIO = 0

        const val PANTALLA_RUTINAS = 1

        const val PANTALLA_CALENDARIO = 2

        const val PANTALLA_PROGRESO = 3

        const val PANTALLA_USUARIOS = 4

        const val PANTALLA_PERFIL = 5

        const val TOTAL_PANTALLAS = 6
    }
}