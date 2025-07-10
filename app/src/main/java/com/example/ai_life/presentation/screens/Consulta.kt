// app/src/main/java/com/example/ai_life/presentation/screens/ConsultaScreen.kt
package com.example.ai_life.presentation.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ai_life.R
import com.example.ai_life.domain.model.Consulta
import com.example.ai_life.domain.model.ConsultaHistorial
import com.example.ai_life.presentation.screens.viewmodel.ConsultaViewModel
import kotlin.math.absoluteValue

@Composable
fun ConsultaScreen(
    navController: NavHostController,
    viewModel: ConsultaViewModel = viewModel()
) {
    val code by viewModel.code.collectAsState()
    val consultas by viewModel.consultas.collectAsState()
    val status by viewModel.status.collectAsState()
    val historial by viewModel.historial.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavPanel(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Cabecera
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
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ingreso de código + lupa
            OutlinedTextField(
                value = code,
                onValueChange = { viewModel.onCodeChange(it) },
                placeholder = { Text("Ingrese Código de Consulta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { viewModel.searchConsulta() }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar y diagnosticar"
                        )
                    }
                }
            )

            // Estado de búsqueda
            status?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(msg, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 1) Al encontrar la consulta root → navegar a diagnóstico
            LaunchedEffect(consultas) {
                if (consultas.isNotEmpty()) {
                    val c = consultas.first()
                    val tempF = c.temperatura.toFloat()
                        .absoluteValue
                        .let { "%.2f".format(it).toFloat() }
                    navController.navigate("diagnostico/${c.code}/${c.bpm}/${c.spo2}/$tempF")
                    viewModel.clearConsultas()
                }
            }

            // 2) Listado del historial guardado
            Text(
                text = "Historial de Diagnósticos",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            historial.forEach { h ->
                HistorialItem(navController, h)
            }
        }
    }
}

@Composable
fun ConsultaItem(navController: NavHostController, consulta: Consulta) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Código: ${consulta.code}", fontSize = 16.sp)
                Text(text = "BPM: ${consulta.bpm}", fontSize = 16.sp)
                Text(text = "SpO2: ${consulta.spo2}", fontSize = 16.sp)
                Text(text = "Temp: ${consulta.temperatura}", fontSize = 16.sp)
            }
            Button(
                onClick = {
                    val tempF = consulta.temperatura.toFloat()
                        .absoluteValue
                        .let { "%.2f".format(it).toFloat() }
                    navController.navigate(
                        "diagnostico/${consulta.code}/${consulta.bpm}/${consulta.spo2}/$tempF"
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF040A7E)),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 5.dp)
            ) {
                Text("Ver", color = Color.White)
            }
        }
        Divider(modifier = Modifier.padding(vertical = 16.dp))
    }
}

@Composable
fun HistorialItem(
    navController: NavHostController,
    h: ConsultaHistorial
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Fecha: ${h.timestamp}", Modifier.weight(1f))
        Button(
            onClick = {
                val diagEnc = Uri.encode(h.diagnosis)
                val tsEnc = Uri.encode(h.timestamp)
                val tempF = h.temperatura.toFloat()
                navController.navigate(
                    "diagnosticoHistorial/${h.code}/${h.bpm}/${h.spo2}/$tempF/$diagEnc/$tsEnc"
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF040A7E)),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 5.dp)
        ) {
            Text("Ver", color = Color.White)
        }
    }
    Divider(Modifier.padding(vertical = 12.dp))
}

@Preview(showBackground = true)
@Composable
fun PreviewConsulta() {
    val navController = rememberNavController()
    ConsultaScreen(navController)
}
