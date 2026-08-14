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
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.Text
import com.example.proyecto.wear.presentation.components.FitTrackScreenContainer
import com.example.proyecto.wear.presentation.components.SectionLabel
import com.example.proyecto.wear.presentation.data.model.RutinaRemota
import com.example.proyecto.wear.presentation.theme.FitTrackGreen
import com.example.proyecto.wear.presentation.theme.FitTrackSurface
import com.example.proyecto.wear.presentation.theme.FitTrackTextSecondary

@Composable
fun RoutineListScreen(
    routines: List<RutinaRemota>,
    cargando: Boolean,
    onSelectRoutine: (RutinaRemota) -> Unit,
    onBack: () -> Unit
) {
    FitTrackScreenContainer {

        SectionLabel(text = "Elige tu rutina", color = FitTrackGreen)

        Spacer(modifier = Modifier.height(8.dp))

        when {

            cargando -> {
                CircularProgressIndicator(
                    modifier = Modifier.height(28.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Pidiendo tus rutinas al teléfono...",
                    fontSize = 10.sp,
                    color = FitTrackTextSecondary
                )
            }

            routines.isEmpty() -> {
                Text(
                    text = "No hay rutinas activas en tu cuenta todavía.",
                    fontSize = 11.sp,
                    color = FitTrackTextSecondary
                )
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
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
                                    text = routine.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "${routine.ejercicios.size} ejercicios",
                                    fontSize = 10.sp,
                                    color = FitTrackTextSecondary
                                )
                            }
                        }
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