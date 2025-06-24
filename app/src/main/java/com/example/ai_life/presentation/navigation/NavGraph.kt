package com.example.ai_life.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ai_life.presentation.screens.loginScreen
import com.example.ai_life.presentation.screens.welcomeScreen

@Composable
fun NavGraph() {
    val navController: NavHostController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { welcomeScreen(navController) }
        composable("login_register"){ loginScreen(navController) }
    }
}