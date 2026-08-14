package com.example.proyecto.ui.perfil
import com.example.movilmensaje.R

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyecto.MainActivity
import com.example.proyecto.data.repository.PerfilRepository
import com.example.proyecto.ui.perfil.editar.EditarPerfilActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.util.Locale

class PerfilFragment : Fragment() {

    private lateinit var tvAvatar: TextView
    private lateinit var tvNombre: TextView
    private lateinit var tvRol: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvPeso: TextView
    private lateinit var tvAltura: TextView
    private lateinit var tvImc: TextView
    private lateinit var tvEstadoImc: TextView
    private lateinit var tvObjetivo: TextView
    private lateinit var tvModeloWatch: TextView
    private lateinit var tvEstadoWatch: TextView
    private lateinit var vPuntoWatch: View

    private lateinit var repository: PerfilRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_perfil,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        repository = PerfilRepository(requireContext())

        inicializarVistas(view)
        configurarEventos()
        cargarPerfil()
    }

    override fun onResume() {
        super.onResume()
        cargarPerfil()
    }

    private fun inicializarVistas(view: View) {

        tvAvatar = view.findViewById(R.id.tvAvatarPerfil)
        tvNombre = view.findViewById(R.id.tvNombrePerfil)
        tvRol = view.findViewById(R.id.tvRolPerfil)
        tvCorreo = view.findViewById(R.id.tvCorreoPerfil)
        tvPeso = view.findViewById(R.id.tvPesoPerfil)
        tvAltura = view.findViewById(R.id.tvAlturaPerfil)
        tvImc = view.findViewById(R.id.tvImcPerfil)
        tvEstadoImc = view.findViewById(R.id.tvEstadoImcPerfil)
        tvObjetivo = view.findViewById(R.id.tvObjetivoPerfil)
        tvModeloWatch = view.findViewById(R.id.tvModeloWatch)
        tvEstadoWatch = view.findViewById(R.id.tvEstadoWatch)
        vPuntoWatch = view.findViewById(R.id.vPuntoWatch)
    }

    private fun configurarEventos() {

        val irAEditar = View.OnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    EditarPerfilActivity::class.java
                )
            )
        }

        view?.findViewById<MaterialButton>(R.id.btnEditarPerfil)
            ?.setOnClickListener(irAEditar)

        view?.findViewById<View>(R.id.btnEditarAvatar)
            ?.setOnClickListener(irAEditar)

        view?.findViewById<MaterialButton>(R.id.btnCerrarSesion)
            ?.setOnClickListener {

                val intent = Intent(
                    requireContext(),
                    MainActivity::class.java
                )

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
            }
    }

    private fun cargarPerfil() {

        viewLifecycleOwner.lifecycleScope.launch {

            val perfil = repository.obtenerPerfil()

            tvAvatar.text =
                perfil.nombre.firstOrNull()
                    ?.uppercase()
                    ?: "U"

            tvNombre.text =
                perfil.nombre.ifBlank { "Usuario FitTrack" }

            tvRol.text = perfil.rol

            tvCorreo.text =
                perfil.email.ifBlank { "Sin correo registrado" }

            tvPeso.text =
                String.format(
                    Locale.getDefault(),
                    "%.1f kg",
                    perfil.peso
                )

            tvAltura.text =
                String.format(
                    Locale.getDefault(),
                    "%.2f m",
                    perfil.altura
                )

            val imc = perfil.calcularImc()

            tvImc.text =
                String.format(
                    Locale.getDefault(),
                    "%.1f",
                    imc
                )

            tvEstadoImc.text =
                descripcionImc(imc)

            tvObjetivo.text = perfil.objetivo

            // Modelo real del smartwatch, en vez del texto fijo
            // "Galaxy Watch" que traía el layout.
            tvModeloWatch.text = perfil.smartwatch

            if (perfil.smartwatchConectado) {

                tvEstadoWatch.text = "Conectado"

                vPuntoWatch.setBackgroundResource(
                    R.drawable.fondo_punto_online
                )

            } else {

                tvEstadoWatch.text = "Desconectado"

                vPuntoWatch.setBackgroundResource(
                    R.drawable.fondo_punto_offline
                )
            }
        }
    }

    private fun descripcionImc(imc: Float): String {
        return when {
            imc <= 0f -> "Sin datos"
            imc < 18.5f -> "Bajo peso"
            imc < 25f -> "Peso saludable"
            imc < 30f -> "Sobrepeso"
            else -> "Obesidad"
        }
    }
}
