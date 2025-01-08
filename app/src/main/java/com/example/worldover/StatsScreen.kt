package com.example.worldover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

@Composable
fun StatsScreen(navController: NavHostController) {
    var stats by remember { mutableStateOf<Map<String, Map<String, Map<String, Int>>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        StatsRepository.fetchStats { fetchedStats ->
            stats = fetchedStats
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2D))
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn {
                items(stats.keys.toList()) { quizType ->
                    StatsCard(quizType = quizType, stats = stats[quizType] ?: emptyMap())
                }
            }
        }
    }
}

@Composable
fun StatsCard(quizType: String, stats: Map<String, Map<String, Int>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E2E3D))
                .padding(16.dp)
        ) {
            Text(
                text = if (quizType == "flags") "Quiz des Drapeaux" else "Quiz des Capitales",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            stats.forEach { (continent, data) ->
                val correct = data["correct"] ?: 0
                val total = data["total"] ?: 0
                val percentage = if (total > 0) (correct * 100 / total) else 0

                Text(
                    text = "$continent : $correct/$total (${percentage}%)",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

