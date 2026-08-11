package com.example.proyecto.wear.presentation.finished

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Text
import com.example.proyecto.wear.presentation.components.AccentRing
import com.example.proyecto.wear.presentation.components.FitTrackScreenContainer
import com.example.proyecto.wear.presentation.components.SectionLabel
import com.example.proyecto.wear.presentation.theme.FitTrackGreen
import com.example.proyecto.wear.presentation.theme.FitTrackRed
import com.example.proyecto.wear.presentation.theme.FitTrackSurface
import com.example.proyecto.wear.presentation.theme.FitTrackTextSecondary
import com.example.proyecto.wear.presentation.utils.TimeUtils

@Composable
fun WorkoutFinishedScreen(
    workoutName: String,
    elapsedSeconds: Long,
    exercises: Int,
    heartRate: Int,
    calories: Int,
    onAccept: () -> Unit
) {
    FitTrackScreenContainer {

        AccentRing(
            progress = 1f,
            color = FitTrackGreen,
            size = 74.dp,
            strokeWidth = 5.dp
        ) {
            Text(
                text = "✓",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = FitTrackGreen
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        SectionLabel(text = "Completado", color = FitTrackGreen)

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = workoutName,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tarjeta de estadísticas, igual al "Resumen de la sesión"
        // del teléfono: tres datos en fila.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(FitTrackSurface)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            EstadisticaFinal(
                valor = TimeUtils.formatSeconds(elapsedSeconds),
                etiqueta = "TIEMPO"
            )

            EstadisticaFinal(
                valor = exercises.toString(),
                etiqueta = "EJERCICIOS"
            )

            EstadisticaFinal(
                valor = "$calories",
                etiqueta = "KCAL"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "♥",
                fontSize = 13.sp,
                color = FitTrackRed
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "$heartRate BPM promedio",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = FitTrackRed
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onAccept,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitTrackGreen,
                contentColor = Color.White
            )
        ) {
            Text(text = "Aceptar", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EstadisticaFinal(
    valor: String,
    etiqueta: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = etiqueta,
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold,
            color = FitTrackTextSecondary
        )
    }
}