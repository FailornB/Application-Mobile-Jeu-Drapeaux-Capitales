package com.example.worldovertest

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import java.text.NumberFormat
import java.util.*

@Composable
fun CountryDetailsScreen(navController: NavHostController, context: Context) {
    var countries by remember { mutableStateOf<List<Country>>(emptyList()) }
    var filteredCountries by remember { mutableStateOf<List<Country>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedContinent by remember { mutableStateOf("All") }
    var sortOrder by remember { mutableStateOf("Default") } // Valeurs : Default, Ascending, Descending

    // Charger les pays depuis l'API locale
    LaunchedEffect(Unit) {
        try {
            val localCountries = ApiLocal.getAllCountries(context)
            countries = localCountries
            filteredCountries = localCountries
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Erreur lors du chargement : ${e.message}"
            isLoading = false
        }
    }

    // Filtrer et trier les pays en fonction de la recherche, du continent et du tri
    LaunchedEffect(searchQuery, selectedContinent, sortOrder) {
        filteredCountries = countries.filter {
            (selectedContinent == "All" || it.continent == selectedContinent) &&
                    (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true))
        }.let { list ->
            when (sortOrder) {
                "Ascending" -> list.sortedBy { it.population ?: 0L }
                "Descending" -> list.sortedByDescending { it.population ?: 0L }
                else -> list
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1E1E2D), Color(0xFF101820))))
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = Color(0xFFFFD700),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                Column {

                    // Barre supérieure avec l'icône retour
                    TopBar(navController)
                    SearchBar(searchQuery) { query -> searchQuery = query }
                    ContinentFilterBar(selectedContinent) { continent -> selectedContinent = continent }
                    SortOrderBar(sortOrder) { order -> sortOrder = order }
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                    ) {
                        items(filteredCountries.size) { index ->
                            val country = filteredCountries[index]
                            CountryCard(country = country)
                            if (index < filteredCountries.size - 1) {
                                Divider(
                                    color = Color.Gray.copy(alpha = 0.2f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2E2E3D))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.navigate(Screen.Home.route) }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Retour",
                tint = Color.White
            )
        }
        Text(
            text = "Détails des pays",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun SearchBar(searchQuery: String, onSearchQueryChanged: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2E2E3D)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            tint = Color.White,
            modifier = Modifier.padding(12.dp)
        )
        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )
    }
}

@Composable
fun ContinentFilterBar(selectedContinent: String, onContinentSelected: (String) -> Unit) {
    val continents = listOf("All", "Africa", "Asia", "Europe", "North America", "South America", "Oceania")
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(continents.size) { index ->
            val continent = continents[index]
            val isSelected = selectedContinent == continent
            Button(
                onClick = { onContinentSelected(continent) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isSelected) Color(0xFFFFD700) else Color(0xFF2E2E3D),
                    contentColor = if (isSelected) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text(text = continent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SortOrderBar(sortOrder: String, onSortOrderChanged: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val sortOptions = listOf("Default", "Ascending", "Descending")
        sortOptions.forEach { order ->
            val isSelected = sortOrder == order
            Button(
                onClick = { onSortOrderChanged(order) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isSelected) Color(0xFFFFD700) else Color(0xFF2E2E3D),
                    contentColor = if (isSelected) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text(text = order, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CountryCard(country: Country) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .animateContentSize()
            .clickable { expanded = !expanded }, // Activation/désactivation en cliquant sur la carte
        elevation = 6.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF2E2E3D))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SubcomposeAsyncImage(
                    model = country.flags,
                    contentDescription = "Drapeau de ${country.name}",
                    loading = {
                        CircularProgressIndicator(
                            color = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    error = {
                        AsyncImage(
                            model = "https://via.placeholder.com/100x60",
                            contentDescription = "Drapeau indisponible",
                            modifier = Modifier.size(100.dp)
                        )
                    },
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.2f))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = country.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    country.capital?.let {
                        Text(
                            text = "Capitale : $it",
                            fontSize = 16.sp,
                            color = Color(0xFFFFD700)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Population : ${formatPopulation(country.population)}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Affichage des détails supplémentaires uniquement si la carte est développée
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                country.currencies?.let { currencies ->
                    val currencyDisplay = currencies.values.joinToString { currency ->
                        "${currency.name ?: "Inconnu"} (${currency.symbol ?: "?"})"
                    }
                    Text(
                        text = "Monnaies : $currencyDisplay",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }

                country.languages?.let { languages ->
                    Text(
                        text = "Langues parlées : ${languages.joinToString()}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}



fun formatPopulation(population: Long?): String {
    return population?.let {
        NumberFormat.getNumberInstance(Locale.FRANCE).format(it)
    } ?: "N/A"
}

