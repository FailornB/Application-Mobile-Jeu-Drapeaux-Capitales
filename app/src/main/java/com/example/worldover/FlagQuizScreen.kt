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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FlagQuizScreen(navController: NavHostController, context: Context) {
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

    // Charger les pays depuis le JSON
    LaunchedEffect(Unit) {
        try {
            countries = ApiLocal.getAllCountries(context)
        } catch (e: Exception) {
            errorMessage = "Erreur lors du chargement des données : ${e.message}"
        }
    }

    // Mettre à jour la question
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E2D))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Nouveau bouton "Retour à l'accueil"
            Button(
                onClick = { navController.navigate(Screen.Home.route) },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Retour à l'accueil",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            if (errorMessage != null) {
                Text("Erreur : $errorMessage", color = Color.Red, modifier = Modifier.padding(16.dp))
            } else if (countries.isEmpty() || currentCountry == null) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                // Barre de progression avec score centré
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

                // Affichage du drapeau avec un indicateur de chargement
                SubcomposeAsyncImage(
                    model = currentCountry?.flags,
                    contentDescription = "Drapeau de ${currentCountry?.name}",
                    loading = {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    },
                    modifier = Modifier
                        .size(400.dp)
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Affichage des boutons (réponses)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    options.forEach { option ->
                        Button(
                            onClick = {
                                if (!showFeedback) {
                                    selectedOption = option
                                    showFeedback = true
                                    if (option == currentCountry) {
                                        score++
                                    }
                                    coroutineScope.launch {
                                        delay(2000)
                                        if (questionCount < 10) {
                                            questionCount++
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = when {
                                    showFeedback && option == currentCountry -> Color.Green
                                    showFeedback && option == selectedOption && option != currentCountry -> Color.Red
                                    else -> MaterialTheme.colors.primary
                                }
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        ) {
                            Text(
                                text = option.name,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Centrer le message de fin
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E2D)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Quiz terminé !",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Votre score final : $score / 10",
                    color = Color(0xFF4CAF50),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { questionCount = 0; score = 0 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Rejouer", fontSize = 16.sp)
                }
                Button(
                    onClick = { navController.navigate(Screen.Home.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Retour au menu", fontSize = 16.sp)
                }
            }
        }
    }
}
