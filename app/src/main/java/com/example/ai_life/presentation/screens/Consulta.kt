// app/src/main/java/com/example/ai_life/presentation/screens/ConsultaScreen.kt
package com.example.ai_life.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavPanel(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Cabecera con logo y nombre
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

            // Campo de ingreso de código y lupa
            OutlinedTextField(
                value = code,
                onValueChange = { viewModel.onCodeChange(it) },
                placeholder = { Text("Ingrese Código de Consulta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        // Al presionar lupa, dispara la búsqueda…
                        viewModel.searchConsulta()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar y diagnosticar"
                        )
                    }
                }
            )

            // Mostrar estado de la búsqueda
            status?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(msg, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Cuando la lista de consultas se actualiza, navegamos automáticamente
            LaunchedEffect(consultas) {
                if (consultas.isNotEmpty()) {
                    val consulta = consultas.first()
                    // Formatear temperatura a Float con dos decimales
                    val tempFloat = consulta.temperatura
                        .toFloat()
                        .absoluteValue
                        .let { "%.2f".format(it).toFloat() }

                    // Navegar a la pantalla de diagnóstico
                    navController.navigate(
                        "diagnostico/${consulta.code}/${consulta.bpm}/${consulta.spo2}/$tempFloat"
                    )
                    // Limpiar para no volver a navegar en recomposiciones
                    viewModel.clearConsultas()
                }
            }

            // Seguimos mostrando el ítem de consulta y el botón "Ver" para uso futuro
            consultas.forEach { consulta ->
                ConsultaItem(navController, consulta)
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
                    // Botón "Ver" reservado para futura funcionalidad
                    val tempFloat = consulta.temperatura
                        .toFloat()
                        .absoluteValue
                        .let { "%.2f".format(it).toFloat() }
                    navController.navigate(
                        "diagnostico/${consulta.code}/${consulta.bpm}/${consulta.spo2}/$tempFloat"
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

@Preview(showBackground = true)
@Composable
fun PreviewConsulta() {
    val navController = rememberNavController()
    ConsultaScreen(navController)
}
