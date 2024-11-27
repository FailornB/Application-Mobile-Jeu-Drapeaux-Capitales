package com.example.worldover

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun CapitalQuizScreen(navController: NavHostController, api: CountriesApi = ApiClient.api) {
    var country by remember { mutableStateOf<Country?>(null) }
    var userAnswer by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var score by remember { mutableStateOf(0) }
    var questionCount by remember { mutableStateOf(0) }

    LaunchedEffect(questionCount) {
        country = api.getAllCountries().random()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (country == null) {
            Text("Chargement...")
        } else if (questionCount < 10) {
            val capital = country!!.capital?.firstOrNull() ?: "Inconnu"
            Text("Quelle est la capitale de ${country!!.name.common} ?", modifier = Modifier.padding(16.dp))
            OutlinedTextField(
                value = userAnswer,
                onValueChange = { userAnswer = it },
                label = { Text("Votre réponse") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                isCorrect = userAnswer.equals(capital, ignoreCase = true)
                if (isCorrect == true) score++
                questionCount++
            }, modifier = Modifier.padding(16.dp)) {
                Text("Valider")
            }
            LinearProgressIndicator(
                progress = questionCount / 10f,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
            isCorrect?.let {
                Text(
                    text = if (it) "Correct !" else "Incorrect. La bonne réponse était $capital.",
                    modifier = Modifier.padding(16.dp)
                )
            }
            Text("Score : $score / 10")
        } else {
            Text("Quiz terminé !", modifier = Modifier.padding(16.dp))
            Text("Votre score final : $score / 10", modifier = Modifier.padding(16.dp))
            Button(onClick = { navController.navigate(Screen.Home.route) }) {
                Text("Retour au menu")
            }
        }
    }
}
