package com.example.proyecto.wear.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.proyecto.wear.presentation.components.FitTrackScreenContainer
import com.example.proyecto.wear.presentation.components.SectionLabel
import com.example.proyecto.wear.presentation.theme.FitTrackGreen
import com.example.proyecto.wear.presentation.theme.FitTrackSurface
import com.example.proyecto.wear.presentation.theme.FitTrackTextSecondary

private data class SesionHistorial(
    val nombre: String,
    val duracion: String
)

@Composable
fun WorkoutHistoryScreen(
    onBack: () -> Unit
) {
    // TODO: hoy son datos fijos de ejemplo. Cuando el historial se
    // guarde de verdad (HistorialEntrenamiento persistido), esta
    // lista se reemplaza por la real.
    val sesiones = listOf(
        SesionHistorial("Push Day", "52 min"),
        SesionHistorial("Pull Day", "48 min"),
        SesionHistorial("Leg Day", "61 min")
    )

    FitTrackScreenContainer {

        SectionLabel(text = "Historial", color = FitTrackGreen)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Últimos entrenamientos",
            fontSize = 10.sp,
            color = FitTrackTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(FitTrackSurface)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.Center
        ) {

            sesiones.forEachIndexed { indice, sesion ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(FitTrackGreen)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = sesion.nombre,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = sesion.duracion,
                        fontSize = 11.sp,
                        color = FitTrackTextSecondary
                    )
                }

                if (indice < sesiones.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                            .height(1.dp)
                            .background(Color(0x22FFFFFF))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitTrackSurface,
                contentColor = Color.White
            )
        ) {
            Text(text = "Volver")
        }
    }
}