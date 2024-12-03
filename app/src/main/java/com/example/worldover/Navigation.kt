package com.example.worldover

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object FlagQuiz : Screen("flag_quiz")
    object CapitalQuiz : Screen("capital_quiz")
    object CountryDetails : Screen("country_details")
}



@Composable
fun AppNavigation(navController: NavHostController, context: Context) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.FlagQuiz.route) { FlagQuizScreen(navController, context) }
        composable(Screen.CapitalQuiz.route) { CapitalQuizScreen(navController) }
        composable(Screen.CountryDetails.route) { CountryDetailsScreen(navController, context) }
    }
}

/*@Composable
fun AppNavigation(navController: NavHostController, context: Context) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.FlagQuiz.route) { FlagQuizScreen(navController, context) }
        //composable(Screen.CapitalQuiz.route) { CapitalQuizScreen(navController, context) }
    }
}*/
