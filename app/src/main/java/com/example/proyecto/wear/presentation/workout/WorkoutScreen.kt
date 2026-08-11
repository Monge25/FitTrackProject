package com.example.proyecto.wear.presentation.workout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import com.example.proyecto.wear.presentation.data.model.Entrenamiento
import com.example.proyecto.wear.presentation.theme.FitTrackGreen
import com.example.proyecto.wear.presentation.theme.FitTrackOrange
import com.example.proyecto.wear.presentation.theme.FitTrackRed
import com.example.proyecto.wear.presentation.theme.FitTrackSurface
import com.example.proyecto.wear.presentation.theme.FitTrackTextSecondary
import com.example.proyecto.wear.presentation.utils.TimeUtils

// Pantalla de entrenamiento activo: cronómetro, frecuencia cardiaca
// y controles de pausar / terminar (continuar vive en PausedWorkoutScreen).
@Composable
fun WorkoutScreen(
    workout: Entrenamiento,
    elapsedSeconds: Long,
    heartRate: Int,
    onCompleteSeries: () -> Unit,
    onPause: () -> Unit,
    onFinish: () -> Unit
) {
    FitTrackScreenContainer {

        SectionLabel(text = workout.nombreRutina, color = FitTrackGreen)

        Spacer(modifier = Modifier.height(4.dp))

        AccentRing(
            progress = workout.serieActual.toFloat() / workout.totalSeries.toFloat(),
            color = FitTrackOrange
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = TimeUtils.formatSeconds(elapsedSeconds),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "TIEMPO DE ENTRENAMIENTO",
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center,
                    color = FitTrackTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = workout.ejercicioActual,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Text(
            text = "Serie ${workout.serieActual}/${workout.totalSeries} · ${workout.repeticiones} rep",
            fontSize = 11.sp,
            color = FitTrackTextSecondary
        )

        Text(
            text = "♥ $heartRate BPM",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = FitTrackRed
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onCompleteSeries,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitTrackGreen,
                contentColor = Color.White
            )
        ) {
            Text(text = "Completar serie", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row {
            Button(
                onClick = onPause,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FitTrackSurface,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Pausar", fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FitTrackRed,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Terminar", fontSize = 11.sp)
            }
        }
    }
}