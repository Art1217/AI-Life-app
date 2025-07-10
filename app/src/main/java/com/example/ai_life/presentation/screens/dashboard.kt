package com.example.ai_life.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.BottomNavigation
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.ai_life.R
import com.example.ai_life.presentation.screens.viewmodel.DashboardViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun dashboardScreen(navController: NavHostController, viewModel: DashboardViewModel = viewModel()
) {
    val nombre    by viewModel.nombreCompleto.collectAsState()
    val localidad by viewModel.localidad.collectAsState()
    val dni       by viewModel.dni.collectAsState()
    // obtener estadísticas
    val enfermedad by viewModel.enfermedadMasComun.collectAsState()
    val sexo       by viewModel.sexoMasPropenso.collectAsState()
    val edadMin    by viewModel.edadMin.collectAsState()
    val edadMax    by viewModel.edadMax.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavPanel(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .background(Color.White)
                .fillMaxSize()
        ) {
            encabezado(nombre, Modifier.weight(1f))
            Text(
                text     = "Localidad: $localidad",
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text     = "DNI: $dni",
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Row(modifier = Modifier.weight(2f).padding(20.dp).fillMaxSize()) {
                Box(modifier = Modifier.weight(1f).fillMaxSize()){
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
                        Text(
                            "Enfermedad más común",
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 14.sp,
                            textAlign  = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            enfermedad.ifBlank { "-" },
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(10.dp))
                Box(modifier = Modifier.weight(1f).fillMaxSize()){
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Sexo más propenso",
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 14.sp,
                            textAlign  = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            sexo.ifBlank { "-" },
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Box(modifier = Modifier.weight(2f).fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Edad más propensa",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp,
                        textAlign  = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        if (edadMin == 0 && edadMax == 0) "-"
                        else "$edadMin años - $edadMax años",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                }
            }
        }

    }
}
@Composable
fun encabezado(name: String, modifier: Modifier = Modifier) {
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
            modifier          = Modifier
                .size(70.dp)
                .clip(CircleShape)
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
fun BottomNavPanel(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    BottomNavigation(
        modifier        = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFF040A7E)
    ) {
        BottomNavigationItem(
            selected = currentRoute == "dashboard",
            onClick  = { navController.navigate("dashboard") },
            icon     = {
                Icon(
                    painter           = painterResource(R.drawable.hospital_1),
                    contentDescription= "Dashboard",
                    tint              = Color.Unspecified,
                    modifier          = Modifier.size(30.dp)
                )
            }
        )
        BottomNavigationItem(
            selected = currentRoute == "consulta",
            onClick  = { navController.navigate("consulta") },
            icon     = {
                Icon(
                    painter           = painterResource(R.drawable.casa_blanca__2__1),
                    contentDescription= "Consulta",
                    tint              = Color.Unspecified,
                    modifier          = Modifier.size(30.dp)
                )
            }
        )
        BottomNavigationItem(
            selected = currentRoute == "perfil",
            onClick  = { navController.navigate("perfil") },
            icon     = {
                Icon(
                    painter           = painterResource(R.drawable.usuario_1),
                    contentDescription= "Perfil",
                    tint              = Color.Unspecified,
                    modifier          = Modifier.size(30.dp)
                )
            }
        )
    }
}

@Composable
@Preview
fun PreviewDashboard() {
    val navController = rememberNavController()
    dashboardScreen(navController)
}