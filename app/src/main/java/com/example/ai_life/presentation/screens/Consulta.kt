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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.IconButton
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ai_life.R
import com.example.ai_life.domain.model.Consulta
import com.example.ai_life.presentation.screens.viewmodel.ConsultaViewModel

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
                            contentDescription = "Buscar"
                        )
                    }
                }
            )
            status?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(msg, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(30.dp))
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