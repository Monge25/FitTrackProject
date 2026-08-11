package com.example.proyecto.wear.presentation.home

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
import com.example.proyecto.wear.presentation.components.FitTrackScreenContainer
import com.example.proyecto.wear.presentation.components.SectionLabel
import com.example.proyecto.wear.presentation.data.model.WorkoutState
import com.example.proyecto.wear.presentation.theme.FitTrackGreen
import com.example.proyecto.wear.presentation.theme.FitTrackSurface
import com.example.proyecto.wear.presentation.theme.FitTrackTextSecondary

@Composable
fun HomeScreen(
    onOpenHistory: () -> Unit,
    onSeeRoutines: () -> Unit
) {
    FitTrackScreenContainer {

        SectionLabel(text = "FitTrack", color = FitTrackGreen)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Supera tus límites",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (WorkoutState.phoneConnected) {
                "● Teléfono conectado"
            } else {
                "○ Esperando teléfono"
            },
            fontSize = 12.sp,
            color = if (WorkoutState.phoneConnected) FitTrackGreen else FitTrackTextSecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Sin entrenamiento activo",
            fontSize = 12.sp,
            color = FitTrackTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onSeeRoutines,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitTrackGreen,
                contentColor = Color.White
            )
        ) {
            Text(text = "Ver rutinas", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = onOpenHistory,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitTrackSurface,
                contentColor = Color.White
            )
        ) {
            Text(text = "Historial")
        }
    }
}