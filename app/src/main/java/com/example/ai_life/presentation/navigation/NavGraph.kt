package com.example.ai_life.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ai_life.presentation.screens.ConsultaScreen
import com.example.ai_life.presentation.screens.DiagnosticoScreen
import com.example.ai_life.presentation.screens.PerfilScreen
import com.example.ai_life.presentation.screens.dashboardScreen
import com.example.ai_life.presentation.screens.loginScreen
import com.example.ai_life.presentation.screens.welcomeScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.ai_life.domain.model.Consulta

@Composable
fun NavGraph() {
    val navController: NavHostController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { welcomeScreen(navController) }
        composable("login_register"){ loginScreen(navController) }
        composable("dashboard"){dashboardScreen(navController)}
        composable(
            route = "diagnostico/{code}/{bpm}/{spo2}/{temp}",
            arguments = listOf(
                navArgument("code") { type = NavType.StringType },
                navArgument("bpm") { type = NavType.IntType },
                navArgument("spo2") { type = NavType.IntType },
                navArgument("temp") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("code") ?: ""
            val bpm = backStackEntry.arguments?.getInt("bpm") ?: 0
            val spo2 = backStackEntry.arguments?.getInt("spo2") ?: 0
            val temp = backStackEntry.arguments?.getFloat("temp") ?: 0f
            DiagnosticoScreen(
                navController,
                Consulta(code, bpm, spo2, temp.toDouble())
            )
        }
        composable("consulta"){ConsultaScreen(navController)}
        composable("perfil"){PerfilScreen(navController)}
    }
}