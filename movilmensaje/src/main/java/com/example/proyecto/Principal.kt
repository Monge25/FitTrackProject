package com.example.proyecto
import com.example.movilmensaje.R

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class Principal : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_principal)

        inicializarComponentes()
        configurarViewPager()
        configurarNavegacionInferior()
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