package com.example.worldovertest

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object QuizSelection : Screen("quiz_selection/{quizType}") {
        fun createRoute(quizType: String) = "quiz_selection/$quizType"
    }
    object FlagQuiz : Screen("flag_quiz/{difficulty}/{continent}") {
        fun createRoute(difficulty: String, continent: String) =
            "flag_quiz/$difficulty/$continent"
    }
    object CapitalQuiz : Screen("capital_quiz/{difficulty}/{continent}") {
        fun createRoute(difficulty: String, continent: String) =
            "capital_quiz/$difficulty/$continent"
    }
    object CountryDetails : Screen("country_details")
    object Login : Screen("login")
    object Register : Screen("register")
    object Stats : Screen("stats")
    object UserProfile : Screen("user_profile")

    // Multijoueur
    object CreateGame : Screen("createGame")
    object JoinGame : Screen("joinGame")
    object GameLobby : Screen("gameLobby/{gameId}") {
        fun createRoute(gameId: String) = "gameLobby/$gameId"
    }
    object GameScreen : Screen("gameScreen/{gameId}") {
        fun createRoute(gameId: String) = "gameScreen/$gameId"
    }
}

@Composable
fun AppNavigation(navController: NavHostController, context: Context) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }

        composable(
            Screen.QuizSelection.route,
            arguments = listOf(navArgument("quizType") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizType = backStackEntry.arguments?.getString("quizType") ?: "unknown"
            QuizSelectionScreen(navController, quizType)
        }

        composable(
            Screen.FlagQuiz.route,
            arguments = listOf(
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("continent") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "Tous"
            val continent = backStackEntry.arguments?.getString("continent") ?: "Tous"
            FlagQuizScreen(navController, context, difficulty, continent)
        }

        composable(Screen.CountryDetails.route) { CountryDetailsScreen(navController, context) }
        composable(Screen.Stats.route) { StatsScreen(navController) }
        composable(Screen.UserProfile.route) { UserProfileScreen(navController) }

        // 🔥 Multijoueur
        composable(Screen.CreateGame.route) { CreateGameScreen(navController, context) }
        composable(Screen.JoinGame.route) { JoinGameScreen(navController) }

        composable(
            Screen.GameLobby.route,
            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId")
            if (!gameId.isNullOrBlank()) {
                GameLobbyScreen(navController, gameId)
            } else {
                navController.navigate(Screen.Home.route)
            }
        }

        composable(
            Screen.GameScreen.route,
            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId")
            if (!gameId.isNullOrBlank()) {
                GameScreen(navController, gameId, context)
            } else {
                navController.navigate(Screen.Home.route)
            }
        }
    }
}
