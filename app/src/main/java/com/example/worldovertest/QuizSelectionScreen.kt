package com.example.worldovertest

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun QuizSelectionScreen(navController: NavHostController, quizType: String) {
    var selectedDifficulty by remember { mutableStateOf("Tous") }
    var selectedContinent by remember { mutableStateOf("Tous") }

    val difficultyMapping = mapOf(
        "Tous" to "All",
        "Facile" to "easy",
        "Moyen" to "medium",
        "Difficile" to "hard"
    )
    val continentMapping = mapOf(
        "Tous" to "All",
        "Afrique" to "Africa",
        "Asie" to "Asia",
        "Europe" to "Europe",
        "Océanie" to "Oceania",
        "Amérique du Nord" to "North America",
        "Amérique du Sud" to "South America"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2D))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Choisissez les options du Quiz",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFC107),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Dropdown pour la difficulté
        DifficultyDropdown(
            selectedOption = selectedDifficulty,
            options = difficultyMapping.keys.toList(),
            onOptionSelected = { selectedDifficulty = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grille des continents
        ContinentSelectionGrid(
            selectedContinent = selectedContinent,
            onContinentSelected = { selectedContinent = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton pour commencer
        Button(
            onClick = {
                val selectedEnglishDifficulty = difficultyMapping[selectedDifficulty] ?: "All"
                val selectedEnglishContinent = continentMapping[selectedContinent] ?: "All"
                navController.navigate(Screen.FlagQuiz.createRoute(selectedEnglishDifficulty, selectedEnglishContinent))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
        ) {
            Text(text = "Commencer le Quiz", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun DifficultyDropdown(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFF2E2E3D), RoundedCornerShape(8.dp))
            .clickable { expanded = true }
            .padding(16.dp)
    ) {
        Text(
            text = selectedOption,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E2E3D))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    },
                    onClick = {
                        expanded = false
                        onOptionSelected(option)
                    }
                )
            }
        }
    }
}

@Composable
fun ContinentSelectionGrid(
    selectedContinent: String,
    onContinentSelected: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            ContinentButton(
                name = "Tous",
                image = "file:///android_asset/globe.png",
                isSelected = selectedContinent == "Tous",
                onClick = { onContinentSelected("Tous") }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            val continents = listOf(
                "Afrique" to "file:///android_asset/africa.png",
                "Asie" to "file:///android_asset/asia.png",
                "Europe" to "file:///android_asset/europa.png",
                "Océanie" to "file:///android_asset/oceania.png",
                "Amérique du Nord" to "file:///android_asset/north_america.png",
                "Amérique du Sud" to "file:///android_asset/south_america.png"
            )

            items(continents) { (name, image) ->
                ContinentButton(
                    name = name,
                    image = image,
                    isSelected = selectedContinent == name,
                    onClick = { onContinentSelected(name) }
                )
            }
        }
    }
}

@Composable
fun ContinentButton(
    name: String,
    image: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFC107) else Color(0xFF2E2E3D)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = image,
                contentDescription = name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
            )
            Text(
                text = name,
                fontSize = 14.sp,
                color = if (isSelected) Color(0xFF1E1E2D) else Color.White
            )
        }
    }
}
