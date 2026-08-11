package com.example.proyecto.wear.presentation.routines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.proyecto.wear.presentation.data.model.Entrenamiento
import com.example.proyecto.wear.presentation.theme.FitTrackGreen
import com.example.proyecto.wear.presentation.theme.FitTrackSurface
import com.example.proyecto.wear.presentation.theme.FitTrackTextSecondary

@Composable
fun RoutineListScreen(
    routines: List<Entrenamiento>,
    onSelectRoutine: (Entrenamiento) -> Unit,
    onBack: () -> Unit
) {
    FitTrackScreenContainer {

        SectionLabel(text = "Elige tu rutina", color = FitTrackGreen)

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            routines.forEach { routine ->
                Button(
                    onClick = { onSelectRoutine(routine) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FitTrackSurface,
                        contentColor = Color.White
                    )
                ) {
                    Column {
                        Text(
                            text = routine.nombreRutina,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${routine.totalEjercicios} ejercicios · ${routine.duracionEstimadaMinutos} min",
                            fontSize = 10.sp,
                            color = FitTrackTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitTrackSurface,
                contentColor = FitTrackTextSecondary
            )
        ) {
            Text(text = "Volver")
        }
    }
}