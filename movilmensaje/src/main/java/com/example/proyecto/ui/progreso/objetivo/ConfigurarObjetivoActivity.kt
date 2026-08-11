package com.example.proyecto.ui.progreso.objetivo
import com.example.movilmensaje.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.data.mock.RutinasCatalog
import com.example.proyecto.data.model.ObjetivoSemanal
import com.example.proyecto.data.repository.ProgresoRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ConfigurarObjetivoActivity :
    AppCompatActivity() {

    private lateinit var etHorasObjetivo:
            TextInputEditText

    private lateinit var etSesionesObjetivo:
            TextInputEditText

    private lateinit var cbLunes: CheckBox
    private lateinit var cbMartes: CheckBox
    private lateinit var cbMiercoles: CheckBox
    private lateinit var cbJueves: CheckBox
    private lateinit var cbViernes: CheckBox
    private lateinit var cbSabado: CheckBox
    private lateinit var cbDomingo: CheckBox

    private lateinit var llRutinasPlaneadas:
            LinearLayout

    private val checkboxesRutinas =
        mutableMapOf<String, CheckBox>()

    private lateinit var btnGuardarObjetivo:
            MaterialButton

    private lateinit var btnCancelarObjetivo:
            MaterialButton

    private lateinit var repository:
            ProgresoRepository

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_configurar_objetivo
        )

        repository =
            ProgresoRepository(this)

        enlazarVistas()

        generarCheckboxesRutinas()

        cargarObjetivoActual()

        configurarEventos()
    }

    private fun enlazarVistas() {
        etHorasObjetivo =
            findViewById(
                R.id.etHorasObjetivo
            )

        etSesionesObjetivo =
            findViewById(
                R.id.etSesionesObjetivo
            )

        cbLunes =
            findViewById(R.id.cbLunes)

        cbMartes =
            findViewById(R.id.cbMartes)

        cbMiercoles =
            findViewById(R.id.cbMiercoles)

        cbJueves =
            findViewById(R.id.cbJueves)

        cbViernes =
            findViewById(R.id.cbViernes)

        cbSabado =
            findViewById(R.id.cbSabado)

        cbDomingo =
            findViewById(R.id.cbDomingo)

        llRutinasPlaneadas =
            findViewById(
                R.id.llRutinasPlaneadas
            )

        btnGuardarObjetivo =
            findViewById(
                R.id.btnGuardarObjetivo
            )

        btnCancelarObjetivo =
            findViewById(
                R.id.btnCancelarObjetivo
            )
    }

    /** Genera un checkbox por cada rutina real del catálogo. */
    private fun generarCheckboxesRutinas() {

        llRutinasPlaneadas.removeAllViews()

        checkboxesRutinas.clear()

        val inflater =
            LayoutInflater.from(this)

        RutinasCatalog.rutinas.forEachIndexed { indice, rutina ->

            val checkBox = CheckBox(this)

            checkBox.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpAPx(52)
                )

            checkBox.text = rutina.nombre
            checkBox.setTextColor(
                android.graphics.Color.parseColor("#FFFFFF")
            )
            checkBox.textSize = 15f
            checkBox.buttonTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#2F7BFF")
                )

            llRutinasPlaneadas.addView(checkBox)

            checkboxesRutinas[rutina.nombre] = checkBox

            if (indice < RutinasCatalog.rutinas.lastIndex) {

                val divisor = View(this)

                divisor.layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    )

                divisor.setBackgroundColor(
                    android.graphics.Color.parseColor("#263242")
                )

                llRutinasPlaneadas.addView(divisor)
            }
        }
    }

    private fun dpAPx(dp: Int): Int {
        return (
                dp * resources.displayMetrics.density
                ).toInt()
    }

    private fun cargarObjetivoActual() {
        val objetivo =
            repository.obtenerObjetivo()

        etHorasObjetivo.setText(
            objetivo.horasObjetivo.toString()
        )

        etSesionesObjetivo.setText(
            objetivo.sesionesObjetivo.toString()
        )

        cbLunes.isChecked =
            "Lunes" in objetivo.diasSeleccionados

        cbMartes.isChecked =
            "Martes" in objetivo.diasSeleccionados

        cbMiercoles.isChecked =
            "Miércoles" in objetivo.diasSeleccionados

        cbJueves.isChecked =
            "Jueves" in objetivo.diasSeleccionados

        cbViernes.isChecked =
            "Viernes" in objetivo.diasSeleccionados

        cbSabado.isChecked =
            "Sábado" in objetivo.diasSeleccionados

        cbDomingo.isChecked =
            "Domingo" in objetivo.diasSeleccionados

        objetivo.rutinasSeleccionadas.forEach { nombre ->
            checkboxesRutinas[nombre]?.isChecked = true
        }
    }

    private fun configurarEventos() {
        btnGuardarObjetivo.setOnClickListener {
            guardarObjetivo()
        }

        btnCancelarObjetivo.setOnClickListener {
            finish()
        }
    }

    private fun guardarObjetivo() {
        val horas =
            etHorasObjetivo.text
                ?.toString()
                ?.trim()
                ?.toFloatOrNull()

        val sesiones =
            etSesionesObjetivo.text
                ?.toString()
                ?.trim()
                ?.toIntOrNull()

        if (
            horas == null ||
            horas <= 0
        ) {
            etHorasObjetivo.error =
                "Ingresa un número de horas válido"

            return
        }

        if (
            sesiones == null ||
            sesiones <= 0
        ) {
            etSesionesObjetivo.error =
                "Ingresa un número de sesiones válido"

            return
        }

        val dias =
            obtenerDiasSeleccionados()

        val rutinas =
            obtenerRutinasSeleccionadas()

        if (dias.isEmpty()) {
            Toast.makeText(
                this,
                "Selecciona al menos un día",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (rutinas.isEmpty()) {
            Toast.makeText(
                this,
                "Selecciona al menos una rutina",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        repository.guardarObjetivo(
            ObjetivoSemanal(
                horasObjetivo = horas,
                sesionesObjetivo = sesiones,
                diasSeleccionados = dias,
                rutinasSeleccionadas = rutinas
            )
        )

        Toast.makeText(
            this,
            "Objetivo semanal guardado",
            Toast.LENGTH_SHORT
        ).show()

        setResult(
            RESULT_OK
        )

        finish()
    }

    private fun obtenerDiasSeleccionados():
            List<String> {

        val dias =
            mutableListOf<String>()

        if (cbLunes.isChecked) {
            dias.add("Lunes")
        }

        if (cbMartes.isChecked) {
            dias.add("Martes")
        }

        if (cbMiercoles.isChecked) {
            dias.add("Miércoles")
        }

        if (cbJueves.isChecked) {
            dias.add("Jueves")
        }

        if (cbViernes.isChecked) {
            dias.add("Viernes")
        }

        if (cbSabado.isChecked) {
            dias.add("Sábado")
        }

        if (cbDomingo.isChecked) {
            dias.add("Domingo")
        }

        return dias
    }

    private fun obtenerRutinasSeleccionadas():
            List<String> {

        return checkboxesRutinas
            .filter { it.value.isChecked }
            .map { it.key }
    }
}