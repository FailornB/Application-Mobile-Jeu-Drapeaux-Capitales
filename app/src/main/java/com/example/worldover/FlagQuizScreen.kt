package com.example.worldover

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FlagQuizScreen(
    navController: NavHostController,
    context: Context,
    difficulty: String,
    continent: String
) {
    var countries by remember { mutableStateOf<List<Country>>(emptyList()) }
    var currentCountry by remember { mutableStateOf<Country?>(null) }
    var options by remember { mutableStateOf<List<Country>>(emptyList()) }
    var selectedOption by remember { mutableStateOf<Country?>(null) }
    var questionCount by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val usedCountries = remember { mutableStateListOf<Country>() }
    val coroutineScope = rememberCoroutineScope()

    // Charger les pays en fonction des critères sélectionnés
    LaunchedEffect(Unit) {
        try {
            val allCountries = ApiLocal.getAllCountries(context)
            countries = allCountries.filter { country ->
                (continent == "All" || country.continent.equals(continent, ignoreCase = true)) &&
                        (difficulty == "All" || country.difficulty.equals(difficulty, ignoreCase = true))
            }
        } catch (e: Exception) {
            errorMessage = "Erreur lors du chargement des données : ${e.message}"
        }
    }

    // Mettre à jour les questions
    LaunchedEffect(questionCount) {
        if (questionCount < 10 && countries.isNotEmpty()) {
            val remainingCountries = countries.filter { it !in usedCountries }
            if (remainingCountries.isNotEmpty()) {
                currentCountry = remainingCountries.random()
                usedCountries.add(currentCountry!!)
                options = (countries - currentCountry!!).shuffled().take(3) + currentCountry!!
                options = options.shuffled()
                selectedOption = null
                showFeedback = false
            } else {
                errorMessage = "Pas assez de pays disponibles pour continuer le quiz."
            }
        }
    }

    if (questionCount < 10) {
        QuizUI(
            countries = countries,
            currentCountry = currentCountry,
            options = options,
            selectedOption = selectedOption,
            showFeedback = showFeedback,
            score = score,
            questionCount = questionCount,
            onOptionSelected = { option ->
                if (!showFeedback) {
                    selectedOption = option
                    val isCorrect = option == currentCountry
                    if (isCorrect) {
                        score++
                    }

                    // ✅ Sauvegarde des statistiques dans Firebase
                    StatsRepository.saveQuizStats(
                        continent = currentCountry?.continent ?: "Unknown",
                        isCorrect = isCorrect,
                        quizType = "flags"
                    )

                    showFeedback = true
                    coroutineScope.launch {
                        delay(2000)
                        questionCount++
                    }
                }
            },
            errorMessage = errorMessage
        )
    } else {
        EndScreen(navController = navController, score = score)
    }
}

@Composable
fun QuizUI(
    countries: List<Country>,
    currentCountry: Country?,
    options: List<Country>,
    selectedOption: Country?,
    showFeedback: Boolean,
    score: Int,
    questionCount: Int,
    onOptionSelected: (Country) -> Unit,
    errorMessage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2D))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (errorMessage != null) {
            Text("Erreur : $errorMessage", color = Color.Red, modifier = Modifier.padding(16.dp))
        } else if (countries.isEmpty() || currentCountry == null) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            // Barre de progression et score
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = questionCount / 10f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF4CAF50),
                    backgroundColor = Color(0xFF2E2E3D)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Score : $score / 10",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Titre de la question
            Text(
                text = "Quel est ce pays ?",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )

            // Affichage du drapeau (agrandi)
            SubcomposeAsyncImage(
                model = currentCountry.flags,
                contentDescription = "Drapeau",
                modifier = Modifier
                    .size(300.dp) // Drapeau plus grand
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Affichage des options
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                options.forEach { option ->
                    AnswerButton(
                        option = option,
                        isCorrect = option == currentCountry,
                        selectedOption = selectedOption,
                        showFeedback = showFeedback,
                        onClick = { onOptionSelected(option) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnswerButton(
    option: Country,
    isCorrect: Boolean,
    selectedOption: Country?,
    showFeedback: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            showFeedback && isCorrect -> Color.Green
            showFeedback && selectedOption == option -> Color.Red
            else -> Color(0xFF3F51B5)
        }
    )

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Text(option.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun EndScreen(navController: NavHostController, score: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF3A1C71), Color(0xFFD76D77), Color(0xFFFFAF7B))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎉 Félicitations ! 🎉",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Votre score final : $score / 10",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate(Screen.Home.route) },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Retour au menu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
