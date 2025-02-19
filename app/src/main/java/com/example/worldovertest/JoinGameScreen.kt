package com.example.worldovertest

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

@Composable
fun JoinGameScreen(navController: NavHostController) {
    var gameId by remember { mutableStateOf("") }
    val firestore = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser ?: return
    val userId = user.uid
    val username = user.displayName ?: "Joueur"
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E2D)).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = gameId,
            onValueChange = { gameId = it },
            label = { Text("Code de la partie", color = Color.Yellow) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Yellow,
                unfocusedBorderColor = Color.Yellow
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                val gameRef = firestore.collection("multiplayer").document(gameId)
                gameRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val players = document.get("players") as? Map<String, Any> ?: emptyMap()

                        if (!players.containsKey(userId)) {
                            gameRef.update("players.$userId", mapOf("username" to username, "score" to 0))
                                .addOnSuccessListener {
                                    navController.navigate(Screen.GameLobby.createRoute(gameId))
                                }
                                .addOnFailureListener {
                                    errorMessage = "Erreur lors de la connexion à la partie."
                                }
                        } else {
                            navController.navigate(Screen.GameLobby.createRoute(gameId))
                        }
                    } else {
                        errorMessage = "Aucune partie trouvée avec ce code."
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFFFFC107)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Rejoindre", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
