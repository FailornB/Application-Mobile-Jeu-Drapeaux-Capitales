package com.example.worldovertest

import com.google.firebase.firestore.FirebaseFirestore

fun updateGameStatus(gameId: String) {
    val firestore = FirebaseFirestore.getInstance()
    val gameRef = firestore.collection("multiplayer").document(gameId)

    gameRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            val answers = document.get("answers") as? Map<String, String> ?: emptyMap()
            val players = document.get("players") as? Map<String, Map<String, Any>> ?: emptyMap()

            // Simule une vérification de réponse correcte
            val correctAnswer = "Paris"  // Exemple de bonne réponse
            val updatedPlayers = mutableMapOf<String, Map<String, Any>>()

            players.forEach { (userId, data) ->
                val currentScore = (data["score"] as? Long)?.toInt() ?: 0
                val userAnswer = answers[userId] ?: ""

                val newScore = if (userAnswer == correctAnswer) currentScore + 1 else currentScore

                updatedPlayers[userId] = mapOf(
                    "username" to data["username"].toString(),
                    "score" to newScore
                )
            }

            // Met à jour les scores et charge une nouvelle question
            gameRef.update(
                mapOf(
                    "players" to updatedPlayers,
                    "answers" to emptyMap<String, String>(),  // Reset des réponses
                    "currentQuestion" to "Nouvelle question..." // Remplacer par une vraie logique
                )
            )
        }
    }
}
