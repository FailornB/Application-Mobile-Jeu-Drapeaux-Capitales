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
import coil.compose.AsyncImage

@Composable
fun FlagQuizScreen(navController: NavHostController, api: CountriesApi = ApiClient.api) {
    var country by remember { mutableStateOf<Country?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var userAnswer by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var score by remember { mutableStateOf(0) }
    var questionCount by remember { mutableStateOf(0) }

    LaunchedEffect(questionCount) {
        isLoading = true
        errorMessage = null
        try {
            val countries = api.getAllCountries()
            country = countries.random()
        } catch (e: Exception) {
            errorMessage = "Erreur lors du chargement des données : ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            isLoading -> {
                Text("Chargement en cours...")
            }
            errorMessage != null -> {
                Text("Erreur : $errorMessage")
                Button(onClick = { questionCount++ }) {
                    Text("Réessayer")
                }
            }
            country != null && questionCount < 10 -> {
                Text("Quel est ce pays ?", modifier = Modifier.padding(16.dp))
                AsyncImage(
                    model = country!!.flags.png,
                    contentDescription = "Drapeau de ${country!!.name.common}",
                    modifier = Modifier.size(200.dp)
                )
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { userAnswer = it },
                    label = { Text("Votre réponse") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    isCorrect = userAnswer.equals(country!!.name.common, ignoreCase = true)
                    if (isCorrect == true) score++
                    questionCount++
                }, modifier = Modifier.padding(16.dp)) {
                    Text("Valider")
                }
                isCorrect?.let {
                    Text(
                        text = if (it) "Correct !" else "Incorrect, la bonne réponse était ${country!!.name.common}.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Text("Score : $score / 10")
            }
            questionCount >= 10 -> {
                Text("Quiz terminé !", modifier = Modifier.padding(16.dp))
                Text("Votre score final : $score / 10", modifier = Modifier.padding(16.dp))
                Button(onClick = { navController.navigate(Screen.Home.route) }) {
                    Text("Retour au menu")
                }
            }
        }
    }
}


