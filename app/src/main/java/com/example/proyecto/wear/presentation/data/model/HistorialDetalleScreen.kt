package com.example.proyecto.wear.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import com.example.proyecto.wear.presentation.data.model.HistorialEntrenamiento
import com.example.proyecto.wear.presentation.theme.FitTrackGreen
import com.example.proyecto.wear.presentation.theme.FitTrackRed
import com.example.proyecto.wear.presentation.theme.FitTrackSurface
import com.example.proyecto.wear.presentation.theme.FitTrackTextSecondary

// Resumen de una sesión ya guardada en el historial. Mismo lenguaje
// visual que WorkoutFinishedScreen (la pantalla que se muestra al
// terminar un entrenamiento en vivo), pero a partir de datos ya
// guardados en vez de un entrenamiento en curso.
@Composable
fun HistorialDetalleScreen(
    sesion: HistorialEntrenamiento,
    onBack: () -> Unit
) {
    FitTrackScreenContainer {

        SectionLabel(text = "Sesión", color = FitTrackGreen)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = sesion.nombreRutina,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(FitTrackSurface)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            EstadisticaSesion(
                valor = sesion.duracion,
                etiqueta = "TIEMPO"
            )

            EstadisticaSesion(
                valor = sesion.ejercicios.toString(),
                etiqueta = "EJERCICIOS"
            )

            EstadisticaSesion(
                valor = "${sesion.calorias}",
                etiqueta = "KCAL"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "♥ ${sesion.frecuenciaPromedio} BPM promedio",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = FitTrackRed
        )

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

@Composable
private fun EstadisticaSesion(
    valor: String,
    etiqueta: String
) {
    androidx.compose.foundation.layout.Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
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