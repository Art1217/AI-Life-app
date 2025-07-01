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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ai_life.R

@Composable
fun PerfilScreen(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavPanel(navController)
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(20.dp).background(Color.White).fillMaxSize()) {
            encabezado(modifier = Modifier.weight(1f))
            Text("Localidad: ", modifier = Modifier.padding(start = 20.dp, end = 20.dp))
            Spacer(modifier = Modifier.padding(10.dp))
            Text("DNI: ",modifier = Modifier.padding(start = 20.dp, end = 20.dp))
            Spacer(modifier = Modifier.padding(20.dp))
            Button(
                onClick = {navController.navigate("consulta")},modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF040A7E)
                    ,contentColor = Color.White)
                    )
                    {
                        Text("Nueva consulta", fontSize = 17.sp)
                    }
            Spacer(modifier = Modifier.padding(20.dp))
            Button(
                onClick = {navController.navigate("dashboard")},modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF040A7E)
                    ,contentColor = Color.White)
            )
            {
                Text("Dashboards", fontSize = 17.sp)
            }
            Spacer(modifier = Modifier.padding(20.dp))
            Button(
                onClick = {   },modifier = Modifier //Implementar Cerrar sesion
                    .fillMaxWidth()
                    .height(50.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF040A7E)
                    ,contentColor = Color.White)
            )
            {
                Text("Cerrar Sesión", fontSize = 17.sp)
            }


        }


    }
}
@Composable
@Preview
fun PreviewPerfil() {
    val navController = rememberNavController()
    PerfilScreen(navController)
}
