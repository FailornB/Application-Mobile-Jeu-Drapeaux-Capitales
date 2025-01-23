package com.example.worldover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2D))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Espacement ajouté au-dessus du logo
        Spacer(modifier = Modifier.height(32.dp))
        // Logo centré et agrandi
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/WorldOver.png") // Chemin vers le logo dans les assets
                .build(),
            contentDescription = "Logo WorldOver",
            modifier = Modifier
                .size(300.dp) // Taille augmentée
                .padding(bottom = 16.dp) // Espacement avec les blocs
        )

        // Blocs en grille 2x2
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            .size(180.dp) // Blocs agrandis
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
                modifier = Modifier.size(64.dp) // Icônes légèrement agrandies
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
