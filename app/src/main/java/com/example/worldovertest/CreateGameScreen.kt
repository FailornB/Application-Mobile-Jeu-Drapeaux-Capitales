package com.example.worldovertest

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@Composable
fun CreateGameScreen(navController: NavHostController, context: Context) {
    val firestore = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser ?: return
    val userId = user.uid
    val username = user.displayName ?: "Joueur"

    var gameId by remember { mutableStateOf(generateGameCode()) }
    val gameRef = firestore.collection("multiplayer").document(gameId)

    LaunchedEffect(Unit) {
        gameRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val allCountries = ApiLocal.getAllCountries(context).shuffled().take(20)
                val questions = allCountries.map { it.name }

                val gameData = mapOf(
                    "host" to userId,
                    "status" to "waiting",
                    "currentQuestionIndex" to 0,
                    "questions" to questions,
                    "players" to mapOf(userId to mapOf("username" to username, "score" to 0))
                )
                gameRef.set(gameData)
            }
        }
    }

    LaunchedEffect(gameId) {
        navController.navigate(Screen.GameLobby.createRoute(gameId))
    }
}


// 🔥 Génération du code de la partie
fun generateGameCode(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val numbers = "0123456789"
    return (1..4).map { chars.random() }.joinToString("") + (1..3).map { numbers.random() }.joinToString("")
}
