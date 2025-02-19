package com.example.worldovertest

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun GameScreen(navController: NavHostController, gameId: String, context: Context) {
    if (gameId.isBlank()) {
        LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) }
        return
    }

    val firestore = FirebaseFirestore.getInstance()
    val gameRef = firestore.collection("multiplayer").document(gameId)

    var allCountries by remember { mutableStateOf<List<Country>>(emptyList()) }
    var questions by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentCountry by remember { mutableStateOf<Country?>(null) }
    var options by remember { mutableStateOf<List<Country>>(emptyList()) }
    var selectedOption by remember { mutableStateOf<Country?>(null) }
    var questionIndex by remember { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf(false) }
    var players by remember { mutableStateOf(mapOf<String, Map<String, Any>>()) }
    var countdown by remember { mutableStateOf(5) }
    var isGameOver by remember { mutableStateOf(false) }
    var winnerName by remember { mutableStateOf("") }
    var isTie by remember { mutableStateOf(false) }
    var newGameId by remember { mutableStateOf("") }
    var listener: ListenerRegistration? by remember { mutableStateOf(null) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val coroutineScope = rememberCoroutineScope()

    // 🔥 Charger les données Firebase une seule fois au démarrage
    LaunchedEffect(Unit) {
        gameRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                questions = document.get("questions") as? List<String> ?: emptyList()
                val loadedCountries = ApiLocal.getAllCountries(context)
                allCountries = loadedCountries

                if (questions.isNotEmpty()) {
                    currentCountry = allCountries.find { it.name == questions[0] }
                    options = (allCountries - currentCountry!!).shuffled().take(3) + currentCountry!!
                    options = options.shuffled()
                }
            }
        }
    }

    // 🔥 Écoute en temps réel des changements dans Firebase
    LaunchedEffect(gameId) {
        listener = gameRef.addSnapshotListener { document, _ ->
            if (document != null && document.exists()) {
                players = document.get("players") as? Map<String, Map<String, Any>> ?: emptyMap()
            }
        }
    }

    DisposableEffect(Unit) { onDispose { listener?.remove() } }

    // 🔄 Gestion du changement de question APRES le countdown
    LaunchedEffect(questionIndex) {
        countdown = 5

        while (countdown > 0) {
            delay(1000L)
            countdown--
        }

        if (questionIndex < 19) {
            val newIndex = questionIndex + 1
            gameRef.update("currentQuestionIndex", newIndex).addOnSuccessListener {
                questionIndex = newIndex
                currentCountry = allCountries.find { it.name == questions[newIndex] }
                options = (allCountries - currentCountry!!).shuffled().take(3) + currentCountry!!
                options = options.shuffled()
                selectedOption = null
                showFeedback = false
            }
        } else {
            isGameOver = true
            // 🔥 Mise à jour de la partie : "finished" + "winner"
            endGame(gameId)
            gameRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val playersData = document.get("players") as? Map<String, Map<String, Any>> ?: emptyMap()
                    val sortedPlayers = playersData.entries.sortedByDescending { it.value["score"] as Long }

                    if (sortedPlayers.size > 1 && sortedPlayers[0].value["score"] == sortedPlayers[1].value["score"]) {
                        isTie = true
                    } else {
                        winnerName = sortedPlayers[0].value["username"] as String
                    }
                }
            }
            delay(5000)
        }
    }

    if (!isGameOver) {
        MultiplayerQuizUI(
            currentCountry = currentCountry,
            options = options,
            selectedOption = selectedOption,
            showFeedback = showFeedback,
            players = players,
            countdown = countdown,
            questionIndex = questionIndex,
            userId = userId,
            onOptionSelected = { option ->
                if (!showFeedback) {
                    selectedOption = option
                    showFeedback = true
                    submitMultiplayerAnswer(gameId, userId, option.name)
                }
            }
        )
    } else {

        GameOverScreen(navController, players, winnerName, isTie, gameId)
    }
}

@Composable
fun MultiplayerQuizUI(
    currentCountry: Country?,
    options: List<Country>,
    selectedOption: Country?,
    showFeedback: Boolean,
    players: Map<String, Map<String, Any>>,
    countdown: Int,
    questionIndex: Int,
    onOptionSelected: (Country) -> Unit,
    userId: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2D))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Question ${questionIndex + 1} / 20", color = Color.White, fontSize = 24.sp)
        Text("Temps restant : $countdown s", color = Color.Red, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        if (currentCountry == null) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Text("Quel est ce pays ?", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            SubcomposeAsyncImage(
                model = currentCountry.flags,
                contentDescription = "Drapeau",
                modifier = Modifier.size(250.dp).padding(16.dp)
            )

            options.forEach { option ->
                MultiplayerAnswerButton(option, option == currentCountry, selectedOption, showFeedback, onOptionSelected)
            }

            Spacer(modifier = Modifier.height(24.dp))

            players[userId]?.let { userData ->
                Text(
                    "Votre score : ${userData["score"]}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun submitMultiplayerAnswer(gameId: String, userId: String, answer: String) {
    val firestore = FirebaseFirestore.getInstance()
    val gameRef = firestore.collection("multiplayer").document(gameId)

    gameRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            val players = document.get("players") as? MutableMap<String, MutableMap<String, Any>> ?: mutableMapOf()
            val questionIndex = (document.getLong("currentQuestionIndex") ?: 0).toInt()
            val correctAnswer = (document.get("questions") as List<String>)[questionIndex]

            if (answer == correctAnswer) {
                val userScore = (players[userId]?.get("score") as? Long ?: 0) + 1
                players[userId]?.set("score", userScore)
            }

            gameRef.update(mapOf("players" to players))
        }
    }
}


@Composable
fun MultiplayerAnswerButton(
    option: Country,
    isCorrect: Boolean,
    selectedOption: Country?,
    showFeedback: Boolean,
    onClick: (Country) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            showFeedback && isCorrect -> Color.Green
            showFeedback && selectedOption == option -> Color.Red
            else -> Color(0xFF3F51B5)
        }
    )

    Button(
        onClick = { onClick(option) },
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp)
    ) {
        Text(text = option.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}


@Composable
fun GameOverScreen(
    navController: NavHostController,
    players: Map<String, Map<String, Any>>,
    winnerName: String,
    isTie: Boolean,
    gameId: String
) {
    val firestore = FirebaseFirestore.getInstance()
    var replayClicked by remember { mutableStateOf(false) }
    var replayGameId by remember { mutableStateOf<String?>(null) }

    // 🔥 Récupérer l'ID de la prochaine partie si elle existe déjà
    LaunchedEffect(gameId) {
        firestore.collection("multiplayer").document(gameId)
            .addSnapshotListener { document, _ ->
                if (document != null && document.exists()) {
                    replayGameId = document.getString("replayGameId")
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF512DA8), Color(0xFFB39DDB)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isTie) {
                Text("🤝 Égalité !", color = Color.Blue, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            } else {
                Text("🏆 Gagnant : ", color = Color(0xFFFFD700), fontSize = 36.sp, fontWeight = FontWeight.Bold)
                Text(winnerName, color = Color(0xFFFFD700), fontSize = 36.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            players.entries.sortedByDescending { it.value["score"] as Long }.forEach { (_, data) ->
                Text(
                    "${data["username"]} : ${data["score"]}",
                    color = if (isTie) Color.Blue else if (data["username"] == winnerName) Color(0xFFFFD700) else Color.LightGray,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                // 🔥 Le premier joueur à cliquer sur "Rejouer" crée la partie
                Button(
                    onClick = {
                        replayClicked = true
                        if (replayGameId == null) {
                            createNewGame(navController, gameId, players)
                        } else {
                            navController.navigate(Screen.GameLobby.createRoute(replayGameId!!))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF512DA8)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("🔄 Rejouer", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { navController.navigate("home") },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("🏠 Accueil", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun endGame(gameId: String) {
    val database = FirebaseDatabase.getInstance("https://worldover-71d92-default-rtdb.europe-west1.firebasedatabase.app").reference
    val gameRef = database.child("multiplayer").child(gameId)

    gameRef.get().addOnSuccessListener { snapshot ->
        if (snapshot.exists()) {
            val players = snapshot.child("players").value as? Map<String, Map<String, Any>> ?: emptyMap()
            val sortedPlayers = players.entries.sortedByDescending { it.value["score"] as Long }

            val winner = if (sortedPlayers.size > 1 && sortedPlayers[0].value["score"] == sortedPlayers[1].value["score"]) {
                "Égalité"
            } else {
                sortedPlayers[0].value["username"].toString()
            }

            gameRef.child("status").setValue("finished")
            gameRef.child("winner").setValue(winner).addOnSuccessListener {
                MultiplayerStatsRepository.updateMultiplayerStats(gameId) // ✅ Mise à jour des stats dans Realtime Database
            }
        }
    }
}




fun createNewGame(navController: NavHostController, oldGameId: String, players: Map<String, Map<String, Any>>) {
    val firestore = FirebaseFirestore.getInstance()
    val gameRef = firestore.collection("multiplayer").document(oldGameId)

    // 🔥 Vérifier si une nouvelle partie a déjà été créée
    gameRef.get().addOnSuccessListener { document ->
        val existingReplayGameId = document.getString("replayGameId")
        if (existingReplayGameId == null) {
            val newGameId = generateGameCode()
            val newGameRef = firestore.collection("multiplayer").document(newGameId)

            val newGameData = mapOf(
                "host" to players.keys.first(),
                "status" to "waiting",
                "currentQuestionIndex" to 0,
                "questions" to ApiLocal.getAllCountries(navController.context).shuffled().take(20).map { it.name },
                "players" to players.mapValues { mapOf("username" to it.value["username"], "score" to 0) }
            )

            // 🔥 Création de la nouvelle partie et mise à jour de l'ancienne partie
            newGameRef.set(newGameData).addOnSuccessListener {
                gameRef.update("replayGameId", newGameId)
                navController.navigate(Screen.GameLobby.createRoute(newGameId))
            }
        } else {
            // 🔥 Si la partie existe déjà, le joueur est redirigé vers elle
            navController.navigate(Screen.GameLobby.createRoute(existingReplayGameId))
        }
    }
}

