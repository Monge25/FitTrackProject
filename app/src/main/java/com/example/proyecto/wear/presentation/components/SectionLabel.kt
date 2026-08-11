package com.example.proyecto.wear.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Text

/**
 * Etiqueta pequeña en mayúsculas usada como encabezado de sección,
 * igual a "SMARTWATCH · CRONÓMETRO" / "DESCANSO" en el documento del proyecto.
 */
@Composable
fun SectionLabel(text: String, color: Color) {
    Text(
        text = text.uppercase(),
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )
}
