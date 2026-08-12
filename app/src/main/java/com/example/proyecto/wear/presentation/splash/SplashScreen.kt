package com.example.proyecto.wear.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Text
import com.example.proyecto.wear.presentation.components.AccentRing
import com.example.proyecto.wear.presentation.theme.FitTrackBackground
import com.example.proyecto.wear.presentation.theme.FitTrackGreen
import com.example.proyecto.wear.presentation.theme.FitTrackTextSecondary
import kotlinx.coroutines.launch

// Pantalla de carga inicial del reloj — mismo lenguaje visual que el
// splash del teléfono (fondo con degradado, resplandor detrás del
// logo, animación de entrada). El anillo circular (el mismo
// componente que ya se usa en entrenamiento/descanso) funciona
// además como indicador de carga real: se llena en 3 segundos y al
// terminar pasa sola a Home.
private const val DURACION_SPLASH_MS = 3000

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val progreso = remember { Animatable(0f) }
    val escala = remember { Animatable(0.7f) }
    val opacidad = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            escala.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        }
        launch {
            opacidad.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500)
            )
        }

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
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF16264D),
                        FitTrackBackground
                    ),
                    radius = 320f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.graphicsLayer {
                scaleX = escala.value
                scaleY = escala.value
                alpha = opacidad.value
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(contentAlignment = Alignment.Center) {

                // Resplandor detrás del anillo
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0x293B82F6))
                )

                AccentRing(
                    progress = progreso.value,
                    color = FitTrackGreen,
                    size = 72.dp,
                    strokeWidth = 5.dp
                ) {
                    // Círculo sólido + ícono de fitness (mancuerna),
                    // igual que en el badge del teléfono. Se dibuja
                    // como vector (no un emoji) para poder pintarlo
                    // de blanco de verdad.
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(FitTrackGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier.size(24.dp)
                        ) {
                            val path = PathParser()
                                .parsePathString(
                                    "M20.57,14.86L22,13.43 20.57,12 17,15.57 8.43,7 12,3.43 " +
                                            "10.57,2 9.14,3.43 7.71,2 5.57,4.14 4.14,2.71 2.71,4.14" +
                                            "l1.43,1.43L2,7.71l1.43,1.43L2,10.57 3.43,12 7,8.43 " +
                                            "15.57,17 12,20.57 13.43,22l1.43,-1.43L16.29,22l2.14,-2.14 " +
                                            "1.43,1.43 1.43,-1.43 -1.43,-1.43L22,16.29z"
                                )
                                .toPath()

                            val escalaIcono = size.width / 24f

                            scale(
                                scaleX = escalaIcono,
                                scaleY = escalaIcono,
                                pivot = Offset.Zero
                            ) {
                                drawPath(
                                    path = path,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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