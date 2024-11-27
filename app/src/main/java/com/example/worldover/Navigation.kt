package com.example.worldover

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object FlagQuiz : Screen("flag_quiz")
    object CapitalQuiz : Screen("capital_quiz")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.FlagQuiz.route) { FlagQuizScreen(navController) }
        composable(Screen.CapitalQuiz.route) { CapitalQuizScreen(navController) }
    }
}
