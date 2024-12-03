package com.example.worldover

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FlagQuizScreen(navController: NavHostController, context: Context) {
    var countries by remember { mutableStateOf<List<Country>>(emptyList()) }
    var currentCountry by remember { mutableStateOf<Country?>(null) }
    var options by remember { mutableStateOf<List<Country>>(emptyList()) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var showAnswerFeedback by remember { mutableStateOf(false) }
    var questionCount by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Load countries from JSON
    LaunchedEffect(Unit) {
        try {
            countries = ApiLocal.getAllCountries(context)
        } catch (e: Exception) {
            errorMessage = "Erreur lors du chargement des données : ${e.message}"
        }
    }

    // Set a new question when questionCount changes
    LaunchedEffect(questionCount) {
        if (countries.isNotEmpty()) {
            currentCountry = countries.random()
            options = (countries - currentCountry!!).shuffled().take(3) + currentCountry!!
            options = options.shuffled()
            isCorrect = null
            showAnswerFeedback = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (errorMessage != null) {
            Text("Erreur : $errorMessage", color = Color.Red, modifier = Modifier.padding(16.dp))
        } else if (countries.isEmpty() || currentCountry == null) {
            Text("Chargement...", modifier = Modifier.padding(16.dp))
        } else {
            // Display the flag and question
            Text(
                text = "Quel est ce pays ?",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )

            AsyncImage(
                model = currentCountry?.flags,
                contentDescription = "Drapeau de ${currentCountry?.name}",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display feedback for the answer
            if (showAnswerFeedback) {
                Text(
                    text = if (isCorrect == true) "Correct !" else "Incorrect. La bonne réponse était ${currentCountry?.name}.",
                    color = if (isCorrect == true) Color.Green else Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Display answer options as a 2x2 grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                options.chunked(2).forEach { rowOptions ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowOptions.forEach { option ->
                            Button(
                                onClick = {
                                    isCorrect = option == currentCountry
                                    if (isCorrect == true) {
                                        score++
                                    }
                                    showAnswerFeedback = true
                                    coroutineScope.launch {
                                        delay(2000)
                                        questionCount++
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (showAnswerFeedback) {
                                        if (option == currentCountry) Color.Green else Color.Red
                                    } else MaterialTheme.colors.primary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = option.name,
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress and score
            LinearProgressIndicator(
                progress = questionCount / 10f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
            Text("Score : $score / 10")
        }

        // Display the final result
        if (questionCount >= 10) {
            Text("Quiz terminé !", modifier = Modifier.padding(16.dp))
            Text("Votre score final : $score / 10", modifier = Modifier.padding(16.dp))
            Button(onClick = { navController.navigate(Screen.Home.route) }) {
                Text("Retour au menu")
            }
        }
    }
}
