package com.example.proyecto.wear.presentation.ready

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
import com.example.proyecto.wear.presentation.data.model.Entrenamiento
import com.example.proyecto.wear.presentation.theme.FitTrackGreen
import com.example.proyecto.wear.presentation.theme.FitTrackSurface
import com.example.proyecto.wear.presentation.theme.FitTrackTextSecondary

// Resumen reducido de la rutina antes de empezar, equivalente
// a la vista de "detalle de rutina" del smartphone pero minimizada
// para el reloj: nombre, duración, ejercicios y primer ejercicio.
@Composable
fun ReadyWorkoutScreen(
    workout: Entrenamiento,
    onStartWorkout: () -> Unit,
    onCancel: () -> Unit
) {
    FitTrackScreenContainer {

        SectionLabel(text = "Resumen", color = FitTrackGreen)

        Spacer(modifier = Modifier.height(6.dp))

        AccentRing(progress = 1f, color = FitTrackGreen) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = workout.nombreRutina,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Text(
                    text = "${workout.duracionEstimadaMinutos} min",
                    fontSize = 11.sp,
                    color = FitTrackTextSecondary
                )
                Text(
                    text = "${workout.totalEjercicios} ejercicios",
                    fontSize = 11.sp,
                    color = FitTrackTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Empieza con: ${workout.ejercicioActual}",
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            color = FitTrackTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onStartWorkout,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitTrackGreen,
                contentColor = Color.White
            )
        ) {
            Text(text = "Iniciar rutina", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitTrackSurface,
                contentColor = Color.White
            )
        ) {
            Text(text = "Elegir otra")
        }
    }
}