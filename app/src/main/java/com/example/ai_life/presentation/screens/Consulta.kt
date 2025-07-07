package com.example.ai_life.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ai_life.R

@Composable
fun ConsultaScreen(navController: NavHostController) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavPanel(navController)
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F3FF), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ingrese Código de Consulta",
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            repeat(2) {
                ConsultaItem(navController)
            }

        }

    }
}

@Composable
fun ConsultaItem(navController: NavHostController) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fecha: DD/MM/AA",
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { navController.navigate("diagnostico") },
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
@Preview
fun PreviewConsulta() {
    val navController = rememberNavController()
    ConsultaScreen(navController)
}