// app/src/main/java/com/example/ai_life/presentation/screens/DiagnosticoScreen.kt
package com.example.ai_life.presentation.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ai_life.R
import com.example.ai_life.domain.model.Consulta
import com.example.ai_life.presentation.screens.viewmodel.DiagnosticoViewModel
import com.example.ai_life.presentation.util.Constants

@Composable
fun DiagnosticoScreen(
    navController: NavHostController,
    code: String,
    bpm: Int,
    spo2: Int,
    temp: Float,
    savedDiagnosis: String? = null,
    savedTimestamp: String? = null,
    savedRecommendation: String? = null,
    viewModel: DiagnosticoViewModel = viewModel()
) {
    val context = LocalContext.current
    val resultIndex by viewModel.resultado.collectAsState()
    val etiqueta   by viewModel.diagnosticoEtiqueta.collectAsState()
    val status     by viewModel.status.collectAsState()

    // Si no hay savedDiagnosis, ejecutamos inferencia
    LaunchedEffect(savedDiagnosis) {
        if (savedDiagnosis == null) {
            viewModel.ejecutarDiagnostico(
                context,
                Consulta(code = code, bpm = bpm, spo2 = spo2, temperatura = temp.toDouble())
            )
        }
    }

    Scaffold(
        bottomBar = { BottomNavPanel(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(70.dp)
                )
                Text(
                    text = "Nombre y Apellido",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // En modo lectura mostramos la fecha
            savedTimestamp?.let { ts ->
                Text("Fecha: ${Uri.decode(ts)}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Métricas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Temperatura: $temp", color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("SpO₂: $spo2", color = Color.Black)
                }
                Text("Ritmo Cardíaco: $bpm", color = Color.Black)
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Diagnóstico
            Text("Diagnóstico:", fontWeight = FontWeight.Bold, color = Color(0xFF040A7E))
            Spacer(modifier = Modifier.height(8.dp))
            val diagText = savedDiagnosis ?: etiqueta ?: "Resultado: clase $resultIndex"
            Text(Uri.decode(diagText), color = Color.Black, modifier = Modifier.padding(bottom = 16.dp))

            // Mensajes de estado solo en inferencia
            if (savedDiagnosis == null) {
                status?.let { Text(it, color = Color.Gray) }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Recomendación
            Text("Recomendación:", fontWeight = FontWeight.Bold, color = Color(0xFF040A7E))
            Spacer(modifier = Modifier.height(8.dp))
            val recText = savedRecommendation
                ?: Constants.DIAGNOSIS_RECOMMENDATIONS[diagText] ?: "[Consulta con su médico]"
            Text(recText, color = Color.Black)
        }
    }
}
