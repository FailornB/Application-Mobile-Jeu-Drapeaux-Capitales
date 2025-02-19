package com.example.worldovertest

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

@Composable
fun UserProfileScreen(navController: NavHostController) {
    val auth = Firebase.auth
    val user = auth.currentUser
    var profileStats by remember { mutableStateOf<Map<String, Map<String, Map<String, Int>>>>(emptyMap()) }
    var isProfileLoading by remember { mutableStateOf(true) }

    // Fetch user stats from Firebase
    LaunchedEffect(Unit) {
        user?.uid?.let { userId ->
            FirebaseDatabase.getInstance("https://worldover-71d92-default-rtdb.europe-west1.firebasedatabase.app")
                .reference.child("users").child(userId).child("stats")
                .get()
                .addOnSuccessListener { snapshot ->
                    profileStats = snapshot.value as? Map<String, Map<String, Map<String, Int>>> ?: emptyMap()
                    isProfileLoading = false
                }
                .addOnFailureListener {
                    isProfileLoading = false
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2D))
    ) {
        // Header with back arrow
        TopAppBar(
            backgroundColor = Color(0xFF2E2E3D),
            title = {
                Text(
                    text = "Mon Profil",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isProfileLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // User Info Section
                    item {
                        UserInfoCard(user = user)
                    }

                    // Stats Section
                    items(profileStats.keys.toList()) { quizType ->
                        ProfileStatsCard(quizType = quizType, stats = profileStats[quizType] ?: emptyMap())
                    }

                    // Action Buttons
                    item {
                        ProfileActions(navController = navController, auth = auth)
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfoCard(user: com.google.firebase.auth.FirebaseUser?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFF2E2E3D)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture Placeholder
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/UserIcon.png") // Replace with your user icon asset
                    .build(),
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Gray, CircleShape)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username
            Text(
                text = user?.displayName ?: "Pseudo inconnu",
                color = Color(0xFFFFC107),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            // Email
            Text(
                text = user?.email ?: "Email inconnu",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ProfileStatsCard(quizType: String, stats: Map<String, Map<String, Int>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        backgroundColor = Color(0xFF2E2E3D)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header for the card
            Text(
                text = if (quizType == "flags") "Quiz des Drapeaux" else "Quiz des Capitales",
                color = Color(0xFFFFC107),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Display each continent's stats
            stats.forEach { (continent, data) ->
                val correct = data["correct"] ?: 0
                val total = data["total"] ?: 0
                val percentage = if (total > 0) (correct * 100 / total) else 0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF3A3A4A),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Continent title
                    Text(
                        text = continent,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    // Correct/Total stats
                    Text(
                        text = "$correct/$total (${if (total > 0) "$percentage%" else "-"})",
                        color = if (total == 0) Color.White else getProfileStatsColor(percentage),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileActions(navController: NavHostController, auth: com.google.firebase.auth.FirebaseAuth) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                auth.signOut()
                navController.navigate("login")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFC107)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Se déconnecter", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val userId = currentUser.uid
                    val databaseRef = FirebaseDatabase.getInstance("https://worldover-71d92-default-rtdb.europe-west1.firebasedatabase.app")
                        .reference.child("users").child(userId)

                    // Supprimer les données utilisateur dans Realtime Database
                    databaseRef.removeValue()
                        .addOnSuccessListener {
                            // Supprimer le compte utilisateur dans Firebase Authentication
                            currentUser.delete()
                                .addOnSuccessListener {
                                    navController.navigate("register")
                                }
                                .addOnFailureListener { error ->
                                    // Gérer les erreurs lors de la suppression du compte
                                    Log.e("ProfileActions", "Erreur lors de la suppression du compte : ${error.message}")
                                }
                        }
                        .addOnFailureListener { error ->
                            // Gérer les erreurs lors de la suppression des données
                            Log.e("ProfileActions", "Erreur lors de la suppression des données : ${error.message}")
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Supprimer le compte", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun getProfileStatsColor(percentage: Int): Color {
    return when {
        percentage < 30 -> Color.Red
        percentage in 30..69 -> Color(0xFFFFA500) // Orange
        else -> Color(0xFF4CAF50) // Green
    }
}
