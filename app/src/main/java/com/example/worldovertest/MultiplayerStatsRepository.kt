package com.example.worldovertest

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object MultiplayerStatsRepository {
    private val database = FirebaseDatabase.getInstance("https://worldover-71d92-default-rtdb.europe-west1.firebasedatabase.app").reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun updateMultiplayerStats(gameId: String) {
        if (userId == null) return

        val gameRef = database.child("multiplayer").child(gameId)

        gameRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val players = snapshot.child("players").value as? Map<String, Map<String, Any>> ?: return@addOnSuccessListener
                val winnerName = snapshot.child("winner").getValue(String::class.java) ?: "Aucun"
                val userStatsRef = database.child("users").child(userId).child("multiplayer_stats")

                val userScore = players[userId]?.get("score") as? Long ?: 0
                val isWinner = players[userId]?.get("username") == winnerName

                userStatsRef.get().addOnSuccessListener { statsSnapshot ->
                    val totalGames = (statsSnapshot.child("totalGames").getValue(Long::class.java) ?: 0) + 1
                    val wins = (statsSnapshot.child("wins").getValue(Long::class.java) ?: 0) + if (isWinner) 1 else 0
                    val totalScore = (statsSnapshot.child("totalScore").getValue(Long::class.java) ?: 0) + userScore
                    val bestScore = maxOf(statsSnapshot.child("bestScore").getValue(Long::class.java) ?: 0, userScore)
                    val winRate = if (totalGames > 0) (wins * 100 / totalGames) else 0
                    val avgScore = if (totalGames > 0) (totalScore / totalGames) else 0

                    val updatedStats = mapOf(
                        "totalGames" to totalGames,
                        "wins" to wins,
                        "winRate" to winRate,
                        "totalScore" to totalScore,
                        "bestScore" to bestScore,
                        "avgScore" to avgScore
                    )

                    userStatsRef.setValue(updatedStats)
                }
            }
        }
    }

    fun fetchMultiplayerStats(onComplete: (Map<String, Long>) -> Unit) {
        if (userId == null) return

        val userStatsRef = database.child("users").child(userId).child("multiplayer_stats")
        userStatsRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val stats = mapOf(
                    "totalGames" to (snapshot.child("totalGames").getValue(Long::class.java) ?: 0),
                    "wins" to (snapshot.child("wins").getValue(Long::class.java) ?: 0),
                    "winRate" to (snapshot.child("winRate").getValue(Long::class.java) ?: 0),
                    "totalScore" to (snapshot.child("totalScore").getValue(Long::class.java) ?: 0),
                    "bestScore" to (snapshot.child("bestScore").getValue(Long::class.java) ?: 0),
                    "avgScore" to (snapshot.child("avgScore").getValue(Long::class.java) ?: 0)
                )
                onComplete(stats)
            } else {
                onComplete(emptyMap())
            }
        }.addOnFailureListener {
            onComplete(emptyMap())
        }
    }
}
