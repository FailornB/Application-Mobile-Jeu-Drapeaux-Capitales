package com.example.worldover

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bienvenue au Quiz Géographique", modifier = Modifier.padding(16.dp))
        Button(
            onClick = { navController.navigate(Screen.FlagQuiz.route) },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Quiz des Drapeaux")
        }
        Button(
            onClick = { navController.navigate(Screen.CapitalQuiz.route) },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Quiz des Capitales")
        }
    }
}
