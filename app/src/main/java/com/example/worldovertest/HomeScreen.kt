package com.example.worldovertest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    var showMultiplayerOptions by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2D))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        // Logo centré et agrandi
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/WorldOver.png") // Chemin vers le logo dans les assets
                .build(),
            contentDescription = "Logo WorldOver",
            modifier = Modifier
                .size(220.dp) // Taille augmentée
                .padding(bottom = 16.dp)

        )

        Spacer(modifier = Modifier.height(16.dp))
        if (showMultiplayerOptions) {
            // Affichage des options "Créer une partie" et "Rejoindre une partie"
            MultiplayerOptions(
                navController = navController,
                onCancel = { showMultiplayerOptions = false }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        HomeCard(
                            icon = Icons.Default.School,
                            title = "Capitales",
                            onClick = { navController.navigate(Screen.CapitalQuiz.route) }
                        )
                        HomeCard(
                            icon = Icons.Default.Flag,
                            title = "Drapeaux",
                            onClick = { navController.navigate(Screen.QuizSelection.route) }
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        HomeCard(
                            icon = Icons.AutoMirrored.Filled.ShowChart,
                            title = "Statistiques",
                            onClick = { navController.navigate(Screen.Stats.route) }
                        )
                        HomeCard(
                            icon = Icons.Default.Public,
                            title = "Apprendre",
                            onClick = { navController.navigate(Screen.CountryDetails.route) }
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        HomeCard(
                            icon = Icons.Default.Group,
                            title = "Multijoueur",
                            onClick = { showMultiplayerOptions = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(150.dp) // Blocs agrandis
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E3D)),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFFFFC107), // Couleur des icônes
                modifier = Modifier.size(56.dp) // Icônes légèrement agrandies
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun MultiplayerOptions(
    navController: NavHostController,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { navController.navigate(Screen.CreateGame.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Créer une partie", color = Color.Black, fontSize = 16.sp)
        }

        Button(
            onClick = { navController.navigate(Screen.JoinGame.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Rejoindre une partie", color = Color.Black, fontSize = 16.sp)
        }

        TextButton(onClick = onCancel) {
            Text("Annuler", color = Color(0xFFFFC107))
        }
    }
}

fun createGame(
    hostId: String,
    hostUsername: String,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val gameId = firestore.collection("multiplayer").document().id  // Générer un ID unique

    val gameData = mapOf(
        "host" to hostId,
        "status" to "waiting",
        "currentQuestion" to null,
        "players" to mapOf(
            hostId to mapOf("username" to hostUsername, "score" to 0)
        ),
        "answers" to emptyMap<String, String>()
    )

    firestore.collection("multiplayer").document(gameId).set(gameData)
        .addOnSuccessListener { onSuccess(gameId) }
        .addOnFailureListener { e -> onFailure(e) }
}

fun joinGame(
    gameId: String,
    userId: String,
    username: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val gameRef = firestore.collection("multiplayer").document(gameId)

    gameRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                gameRef.update("players.$userId", mapOf("username" to username, "score" to 0))
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e) }
            } else {
                onFailure(Exception("La partie n'existe pas."))
            }
        }
        .addOnFailureListener { e -> onFailure(e) }
}


@Composable
fun ProfileButton(onClick: () -> Unit) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .size(180.dp)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E3D)),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("file:///android_asset/UserIcon.png") // Image UserIcon.png
                        .build(),
                    contentDescription = "Profil",
                    modifier = Modifier.size(64.dp) // Taille de l'image
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Profil",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
