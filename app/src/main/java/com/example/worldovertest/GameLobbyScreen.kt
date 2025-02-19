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
import com.google.firebase.firestore.ListenerRegistration

@Composable
fun GameLobbyScreen(navController: NavHostController, gameId: String) {
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var players by remember { mutableStateOf(mapOf<String, Map<String, Any>>()) }
    var hostId by remember { mutableStateOf("") }
    var listener: ListenerRegistration? by remember { mutableStateOf(null) }

    val gameRef = firestore.collection("multiplayer").document(gameId)

    LaunchedEffect(gameId) {
        listener = gameRef.addSnapshotListener { document, _ ->
            if (document != null && document.exists()) {
                players = document.get("players") as? Map<String, Map<String, Any>> ?: emptyMap()
                hostId = document.getString("host") ?: ""

                val gameStatus = document.getString("status") ?: "waiting"
                if (gameStatus == "started") {
                    navController.navigate(Screen.GameScreen.createRoute(gameId))
                }
            }
        }
    }

    DisposableEffect(Unit) { onDispose { listener?.remove() } }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E2D)).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Salle d’attente", fontSize = 24.sp, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Code de la partie : $gameId", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Joueurs connectés :", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        players.forEach { (_, data) -> Text("${data["username"]}", color = Color.White, fontSize = 18.sp) }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔥 SEUL L'HÔTE PEUT LANCER
        if (players.size >= 2 && userId == hostId) {
            Button(
                onClick = { gameRef.update("status", "started") },
                colors = ButtonDefaults.buttonColors(Color(0xFFFFC107)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Démarrer la partie", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

