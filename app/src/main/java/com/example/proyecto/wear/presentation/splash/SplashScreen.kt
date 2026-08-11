package com.example.proyecto.wear.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Text
import com.example.proyecto.wear.presentation.components.AccentRing
import com.example.proyecto.wear.presentation.theme.FitTrackBackground
import com.example.proyecto.wear.presentation.theme.FitTrackGreen
import com.example.proyecto.wear.presentation.theme.FitTrackTextSecondary

// Pantalla de carga inicial del reloj: el mismo anillo circular que ya
// se usa en las pantallas de entrenamiento/descanso, aquí funcionando
// como indicador de carga real — se llena en 3 segundos y al terminar
// pasa sola a Home.
private const val DURACION_SPLASH_MS = 3000

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val progreso = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progreso.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = DURACION_SPLASH_MS,
                easing = LinearEasing
            )
        )
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FitTrackBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AccentRing(
                progress = progreso.value,
                color = FitTrackGreen,
                size = 72.dp,
                strokeWidth = 5.dp
            ) {
                Text(
                    text = "⚡",
                    fontSize = 24.sp,
                    color = FitTrackGreen
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "FitTrack",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Supera tus límites",
                fontSize = 10.sp,
                color = FitTrackTextSecondary
            )
        }
    }
}