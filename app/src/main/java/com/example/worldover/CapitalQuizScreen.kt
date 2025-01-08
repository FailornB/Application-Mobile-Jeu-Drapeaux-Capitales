package com.example.worldover

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CapitalQuizScreen(navController: NavHostController, api: CountriesApi = ApiClient.api) {
    val coroutineScope = rememberCoroutineScope()

    var currentQuestion by remember { mutableStateOf<Question?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var questionCount by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf(false) }
    var apiError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            currentQuestion = loadQuestion(api)
            isLoading = false
        } catch (e: Exception) {
            apiError = true
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E2D)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else if (apiError) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E2D)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Échec du chargement du quiz. Veuillez réessayer.",
                color = Color.Red,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else if (questionCount < 10) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E2D)).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LinearProgressIndicator(
                progress = questionCount / 10f,
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = Color(0xFF4CAF50),
                backgroundColor = Color(0xFF2E2E3D)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Score: $score / 10",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "${currentQuestion?.countryName}",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                currentQuestion?.options?.forEach { option ->
                    AnswerButton(
                        option = option,
                        correctAnswer = currentQuestion!!.correctAnswer,
                        selectedOption = selectedOption,
                        showFeedback = showFeedback,
                        onClick = {
                            if (!showFeedback) {
                                selectedOption = option
                                val isCorrect = option == currentQuestion!!.correctAnswer
                                if (isCorrect) score++

                                StatsRepository.saveQuizStats(
                                    continent = currentQuestion!!.continent ?: "Unknown",
                                    isCorrect = isCorrect,
                                    quizType = "capitals"
                                )

                                showFeedback = true

                                coroutineScope.launch {
                                    delay(1500)
                                    currentQuestion = loadQuestion(api)
                                    questionCount++
                                    selectedOption = null
                                    showFeedback = false
                                }
                            }
                        }
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF3A1C71), Color(0xFFD76D77), Color(0xFFFFAF7B))
                )
            ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🎉 Félicitations ! 🎉",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Votre score final : $score / 10",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { questionCount = 0; score = 0 },
                    modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
                ) {
                    Text("Rejouer", fontSize = 18.sp, color = Color.White)
                }
                Button(
                    onClick = { navController.navigate(Screen.Home.route) },
                    modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retour au Menu", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AnswerButton(
    option: String,
    correctAnswer: String,
    selectedOption: String?,
    showFeedback: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            showFeedback && option == correctAnswer -> Color.Green
            showFeedback && option == selectedOption -> Color.Red
            else -> Color(0xFF3F51B5)
        }
    )

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)
    ) {
        Text(option, color = Color.White, fontSize = 16.sp)
    }
}

data class Question(
    val countryName: String,
    val correctAnswer: String,
    val options: List<String>,
    val continent: String?
)

suspend fun loadQuestion(api: CountriesApi): Question {
    val countries = withContext(Dispatchers.IO) { api.getAllCountries() }
        .filter { it.capital?.isNotEmpty() == true }
    val country = countries.randomOrNull() ?: throw Exception("No countries available")
    val options = generateOptions(countries, country.capital!!)
    return Question(
        countryName = country.name,
        correctAnswer = country.capital!!,
        options = options,
        continent = country.continent ?: "Unknown"
    )
}

fun generateOptions(countries: List<Country>, correctAnswer: String): List<String> {
    val incorrectOptions = countries
        .filter { it.capital != correctAnswer && !it.capital.isNullOrEmpty() }
        .shuffled()
        .take(3)
        .map { it.capital!! }
    return (incorrectOptions + correctAnswer).shuffled()
}
