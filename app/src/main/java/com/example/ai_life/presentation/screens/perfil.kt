package com.example.ai_life.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ai_life.presentation.screens.viewmodel.DashboardViewModel

@Composable
fun PerfilScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = viewModel()
) {
    val nombre    by viewModel.nombreCompleto.collectAsState()
    val localidad by viewModel.localidad.collectAsState()
    val dni       by viewModel.dni.collectAsState()
    val status    by viewModel.status.collectAsState()

    Scaffold(
        modifier  = Modifier.fillMaxSize(),
        bottomBar = { BottomNavPanel(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp)
                .background(Color.White)
                .fillMaxSize()
        ) {
            Desencabezado(nombre, Modifier.weight(1f))

            status?.let {
                Text(
                    text     = it,
                    color    = Color.Gray,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick    = { navController.navigate("consulta") },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                elevation  = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                colors     = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF040A7E),
                    contentColor   = Color.White
                )
            ) {
                Text("Nueva consulta", fontSize = 17.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick    = { navController.navigate("dashboard") },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                elevation  = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                colors     = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF040A7E),
                    contentColor   = Color.White
                )
            ) {
                Text("Dashboards", fontSize = 17.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick    = { /* TODO: implementar Cerrar Sesión */ },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                elevation  = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                colors     = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF040A7E),
                    contentColor   = Color.White
                )
            ) {
                Text("Cerrar Sesión", fontSize = 17.sp)
            }
        }
    }
}

@Composable
fun Desencabezado(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val logo = painterResource(id = R.drawable.logo)
        Image(
            painter           = logo,
            contentDescription= null,
            modifier          = Modifier.size(70.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text      = name.ifBlank { "Nombre y Apellido" },
            fontWeight= FontWeight.SemiBold,
            fontSize  = 18.sp,
            color     = Color.Black
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPerfil() {
    val navController = rememberNavController()
    PerfilScreen(navController)
}