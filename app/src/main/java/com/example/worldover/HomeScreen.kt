package com.example.worldover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext

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
        // Logo
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/WorldOver.png") // Chemin vers l'image dans assets
                .build(),
            contentDescription = "Logo",
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        )

        Button(
            onClick = { navController.navigate(Screen.Stats.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Voir les Statistiques", fontSize = 16.sp)
        }

        // Buttons
        Button(
            onClick = { navController.navigate(Screen.FlagQuiz.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Quiz des Drapeaux", fontSize = 16.sp)
        }

        Button(
            onClick = { navController.navigate(Screen.CapitalQuiz.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Quiz des Capitales", fontSize = 16.sp)
        }

        Button(
            onClick = { navController.navigate(Screen.CountryDetails.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = "Country Details",
                tint = Color.White,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Détails des Pays", fontSize = 16.sp)
        }
    }
}
