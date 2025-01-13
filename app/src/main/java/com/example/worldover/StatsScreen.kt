package com.example.worldover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun StatsScreen(navController: NavHostController) {
    var stats by remember { mutableStateOf<Map<String, Map<String, Map<String, Int>>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch stats from the repository
    LaunchedEffect(Unit) {
        StatsRepository.fetchStats { fetchedStats ->
            stats = fetchedStats
            isLoading = false
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
                    text = "Statistiques",
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
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(stats.keys.toList()) { quizType ->
                        StatsCard(quizType = quizType, stats = stats[quizType] ?: emptyMap())
                    }
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
                        color = if (total == 0) Color.White else getPercentageColor(percentage),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// Function to determine color based on percentage
@Composable
fun getPercentageColor(percentage: Int): Color {
    return when {
        percentage < 30 -> Color.Red
        percentage in 30..69 -> Color(0xFFFFA500) // Orange
        else -> Color(0xFF4CAF50) // Green
    }
}
