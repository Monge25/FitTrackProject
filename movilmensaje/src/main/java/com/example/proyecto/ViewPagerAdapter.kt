package com.example.proyecto

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyecto.ui.calendario.CalendarioFragment
import com.example.proyecto.ui.dashboard.DashboardFragment
import com.example.proyecto.ui.perfil.PerfilFragment
import com.example.proyecto.ui.progreso.ProgresoFragment
import com.example.proyecto.ui.rutinas.RutinasFragment
import com.example.proyecto.ui.usuarios.UsuariosFragment

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {

        return when (position) {

            Principal.PANTALLA_INICIO ->
                DashboardFragment()

            Principal.PANTALLA_RUTINAS ->
                RutinasFragment()

            Principal.PANTALLA_CALENDARIO ->
                CalendarioFragment()

            Principal.PANTALLA_PROGRESO ->
                ProgresoFragment()

            Principal.PANTALLA_USUARIOS ->
                UsuariosFragment()

            Principal.PANTALLA_PERFIL ->
                PerfilFragment()

            else ->
                DashboardFragment()
        }
    }
}