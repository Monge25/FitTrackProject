package com.example.proyecto.wear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.MaterialTheme

val FitTrackBackground = Color(0xFF070B12)   // fondo general (phone: #070B12)
val FitTrackSurface = Color(0xFF151B26)      // tarjetas / botones secundarios (phone: #151B26)
val FitTrackGreen = Color(0xFF2F7BFF)        // acento principal / acciones (phone: #2F7BFF)
val FitTrackOrange = Color(0xFF2F7BFF)       // anillo de progreso (mismo acento azul)
val FitTrackBlue = Color(0xFF5B9BFF)         // descanso / acentos claros (phone: #5B9BFF)
val FitTrackRed = Color(0xFFFF6B81)          // frecuencia cardiaca / finalizar (phone: #FF6B81)
val FitTrackTextSecondary = Color(0xFF8D99A8) // texto secundario (phone: #8D99A8)
val FitTrackTrack = Color(0xFF263242)        // fondo del anillo / bordes (phone: #263242)

@Composable
fun ProyectoTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme.copy(
        primary = FitTrackGreen,
        onPrimary = Color.White,
        secondary = FitTrackOrange,
        onSecondary = Color.White,
        tertiary = FitTrackBlue,
        onTertiary = Color.White,
        error = FitTrackRed,
        onError = Color.White,
        background = FitTrackBackground,
        onBackground = Color.White,
        onSurface = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}