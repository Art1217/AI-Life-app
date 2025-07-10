package com.example.ai_life.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ai_life.presentation.screens.ConsultaScreen
import com.example.ai_life.presentation.screens.DiagnosticoScreen
import com.example.ai_life.presentation.screens.DiagnosticoHistorialScreen

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
        // Ruta para inferencia al pulsar lupa
        composable(
            "diagnostico/{code}/{bpm}/{spo2}/{temp}",
            arguments = listOf(
                navArgument("code"){ type = NavType.StringType },
                navArgument("bpm") { type = NavType.IntType },
                navArgument("spo2"){ type = NavType.IntType },
                navArgument("temp"){ type = NavType.FloatType }
            )
        ) { bc ->
            val code = bc.arguments!!.getString("code")!!
            val bpm  = bc.arguments!!.getInt("bpm")
            val spo2 = bc.arguments!!.getInt("spo2")
            val temp = bc.arguments!!.getFloat("temp")
            DiagnosticoScreen(
                navController,
                code = code,
                bpm = bpm,
                spo2 = spo2,
                temp = temp,
                savedDiagnosis = null,
                savedTimestamp = null
            )
        }

        // Ruta para modo lectura (botón Ver)
        composable(
            "diagnosticoHistorial/{code}/{bpm}/{spo2}/{temp}/{diagnosis}/{timestamp}",
            arguments = listOf(
                navArgument("code"){ type = NavType.StringType },
                navArgument("bpm") { type = NavType.IntType },
                navArgument("spo2"){ type = NavType.IntType },
                navArgument("temp"){ type = NavType.FloatType },
                navArgument("diagnosis"){ type = NavType.StringType },
                navArgument("timestamp"){ type = NavType.StringType }
            )
        ) { bc ->
            val code      = bc.arguments!!.getString("code")!!
            val bpm       = bc.arguments!!.getInt("bpm")
            val spo2      = bc.arguments!!.getInt("spo2")
            val temp      = bc.arguments!!.getFloat("temp")
            // Décodifica los parámetros que fueron URI-encoded
            val diagnosis = Uri.decode(bc.arguments!!.getString("diagnosis")!!)
            val timestamp = Uri.decode(bc.arguments!!.getString("timestamp")!!)
            DiagnosticoScreen(
                navController,
                code = code,
                bpm = bpm,
                spo2 = spo2,
                temp = temp,
                savedDiagnosis = diagnosis,
                savedTimestamp = timestamp
            )
        }
        composable("consulta"){ConsultaScreen(navController)}
        composable("perfil"){PerfilScreen(navController)}
    }
}