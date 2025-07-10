package com.example.ai_life.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ai_life.R
import com.example.ai_life.domain.model.Consulta
import com.example.ai_life.presentation.screens.BottomNavPanel
import com.example.ai_life.presentation.screens.viewmodel.DiagnosticoViewModel

@Composable
fun DiagnosticoScreen(
    navController: NavHostController,
    consulta: Consulta,
    viewModel: DiagnosticoViewModel = viewModel()
) {
    val context = LocalContext.current
    val resultIndex by viewModel.resultado.collectAsState()
    val etiqueta by viewModel.diagnosticoEtiqueta.collectAsState()
    val status by viewModel.status.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.ejecutarDiagnostico(context, consulta)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                    color = Color.Black,
                    modifier = Modifier
                        .border(1.dp, Color(0xFFBFE2FD))
                        .padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Muestra los datos de la consulta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Temperatura: ${consulta.temperatura}", color = Color.Black)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("SpO₂: ${consulta.spo2}", color = Color.Black)
                }
                Text("Ritmo Cardiaco: ${consulta.bpm}", color = Color.Black)
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Sección de diagnóstico
            Text(
                text = "Diagnóstico:",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF040A7E)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Ahora mostramos la etiqueta decodificada si existe, si no el índice o mensaje de carga
            Text(
                text = when {
                    etiqueta != null -> etiqueta!!
                    resultIndex != null -> "Resultado: clase $resultIndex"
                    else -> "[ Sin resultado ]"
                },
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mensajes de estado intermedios
            status?.let {
                Text(it, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Recomendación:",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF040A7E)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "[Consulte con su médico]",
                color = Color.Black
            )
        }
    }
}

@Composable
@Preview
fun PreviewDiagnostico() {
    val navController = rememberNavController()
    DiagnosticoScreen(
        navController,
        Consulta(code = "123", bpm = 80, spo2 = 95, temperatura = 36.5)
    )
}
