// app/src/main/java/com/example/ai_life/presentation/screens/DiagnosticoScreen.kt
package com.example.ai_life.presentation.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ai_life.presentation.screens.viewmodel.DashboardViewModel
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
    diagViewModel: DiagnosticoViewModel = viewModel(),
    dashViewModel: DashboardViewModel = viewModel()
) {
    val context = LocalContext.current

    // usuario
    val nombreCompleto by dashViewModel.nombreCompleto.collectAsState()

    // diagnóstico
    val resultIndex by diagViewModel.resultado.collectAsState()
    val etiqueta   by diagViewModel.diagnosticoEtiqueta.collectAsState()
    val status     by diagViewModel.status.collectAsState()

    LaunchedEffect(savedDiagnosis) {
        if (savedDiagnosis == null) {
            diagViewModel.ejecutarDiagnostico(
                context,
                Consulta(code = code, bpm = bpm, spo2 = spo2, temperatura = temp.toDouble())
            )
        }
    }

    Scaffold(
        bottomBar = { BottomNavPanel(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header dinámico
            Row(
                modifier             = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment    = Alignment.CenterVertically
            ) {
                Image(
                    painter           = painterResource(R.drawable.logo),
                    contentDescription= "Logo",
                    modifier          = Modifier.size(70.dp)
                )
                Text(
                    text      = nombreCompleto.ifBlank { "Nombre y Apellido" },
                    fontWeight= FontWeight.Bold,
                    fontSize  = 18.sp,
                    color     = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha (modo lectura)
            savedTimestamp?.let { ts ->
                Text(
                    text  = "Fecha: ${Uri.decode(ts)}",
                    style = typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Métricas
            Row(
                modifier             = Modifier.fillMaxWidth(),
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
            Text(
                text     = Uri.decode(diagText),
                color    = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

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
                ?: Constants.DIAGNOSIS_RECOMMENDATIONS[diagText]
                ?: "[Consulta con su médico]"
            Text(recText, color = Color.Black)
        }
    }
}
