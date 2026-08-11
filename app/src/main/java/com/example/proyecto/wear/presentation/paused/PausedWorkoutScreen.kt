package com.example.proyecto.wear.presentation.paused

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.example.proyecto.wear.presentation.theme.FitTrackOrange
import com.example.proyecto.wear.presentation.theme.FitTrackSurface
import com.example.proyecto.wear.presentation.theme.FitTrackTextSecondary
import com.example.proyecto.wear.presentation.utils.TimeUtils

@Composable
fun PausedWorkoutScreen(
    elapsedSeconds: Long,
    onResume: () -> Unit,
    onFinish: () -> Unit
) {
    FitTrackScreenContainer {

        SectionLabel(text = "Entrenamiento pausado", color = FitTrackTextSecondary)

        Spacer(modifier = Modifier.height(6.dp))

        AccentRing(progress = 0.5f, color = FitTrackOrange) {
            Text(
                text = TimeUtils.formatSeconds(elapsedSeconds),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onResume,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitTrackOrange,
                contentColor = Color.White
            )
        ) {
            Text(text = "Reanudar", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitTrackSurface,
                contentColor = Color.White
            )
        ) {
            Text(text = "Finalizar")
        }
    }
}