package com.example.proyecto.wear.presentation.rest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Text
import com.example.proyecto.wear.presentation.components.AccentRing
import com.example.proyecto.wear.presentation.components.FitTrackScreenContainer
import com.example.proyecto.wear.presentation.components.SectionLabel
import com.example.proyecto.wear.presentation.theme.FitTrackBlue
import com.example.proyecto.wear.presentation.theme.FitTrackRed
import com.example.proyecto.wear.presentation.theme.FitTrackSurface
import com.example.proyecto.wear.presentation.theme.FitTrackTextSecondary
import com.example.proyecto.wear.presentation.utils.TimeUtils

// Duración de referencia del descanso, usada solo para dibujar el avance del anillo
private const val REST_DURATION_REFERENCE = 45f

@Composable
fun RestScreen(
    restSeconds: Int,
    nextExercise: String,
    heartRate: Int,
    onSkipRest: () -> Unit
) {
    FitTrackScreenContainer {

        SectionLabel(text = "Descanso", color = FitTrackBlue)

        Spacer(modifier = Modifier.height(6.dp))

        AccentRing(
            progress = (restSeconds / REST_DURATION_REFERENCE).coerceIn(0f, 1f),
            color = FitTrackBlue
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = TimeUtils.formatRestSeconds(restSeconds),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Siguiente: $nextExercise",
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    color = FitTrackTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "♥ $heartRate BPM",
            fontSize = 12.sp,
            color = FitTrackRed
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSkipRest,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitTrackSurface,
                contentColor = FitTrackBlue
            )
        ) {
            Text(text = "Omitir")
        }
    }
}
