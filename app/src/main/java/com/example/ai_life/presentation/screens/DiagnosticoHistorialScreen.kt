// app/src/main/java/com/example/ai_life/presentation/screens/DiagnosticoHistorialScreen.kt
package com.example.ai_life.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun DiagnosticoHistorialScreen(
    navController: NavHostController,
    code: String,
    bpm: Int,
    spo2: Int,
    temp: Float,
    diagnosis: String,
    timestamp: String
) {
    Scaffold(
        topBar = { /* tu AppBar */ },
        bottomBar = { BottomNavPanel(navController) }
    ) { padding ->
        Column(Modifier.padding(16.dp)) {
            Text(text = "Fecha: $timestamp", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Text("Código: $code")
            Text("BPM: $bpm")
            Text("SpO₂: $spo2")
            Text("Temp: %.2f".format(temp))
            Spacer(Modifier.height(24.dp))
            Text("Diagnóstico:", style = MaterialTheme.typography.titleMedium)
            Text(diagnosis, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))
            Text("Recomendación:", style = MaterialTheme.typography.titleMedium)
            Text("[Consulta con su médico]", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
