package com.example.worldover

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

object StatsRepository {
    private val database = FirebaseDatabase.getInstance("https://worldover-71d92-default-rtdb.europe-west1.firebasedatabase.app").reference
    private val currentUser = Firebase.auth.currentUser

    fun saveQuizStats(continent: String, isCorrect: Boolean, quizType: String) {
        val userId = currentUser?.uid ?: return
        val path = "users/$userId/stats/$quizType/$continent"

        database.child(path).get().addOnSuccessListener { snapshot ->
            val currentCorrect = snapshot.child("correct").getValue(Int::class.java) ?: 0
            val currentTotal = snapshot.child("total").getValue(Int::class.java) ?: 0

            val updatedStats = mapOf(
                "correct" to if (isCorrect) currentCorrect + 1 else currentCorrect,
                "total" to currentTotal + 1
            )

            database.child(path).setValue(updatedStats)
        }
    }

    fun fetchStats(onComplete: (Map<String, Map<String, Map<String, Int>>>) -> Unit) {
        val userId = currentUser?.uid ?: return
        database.child("users/$userId/stats").get().addOnSuccessListener { snapshot ->
            val stats = snapshot.children.associate { quizType ->
                val quizStats = quizType.children.associate { continent ->
                    val continentStats = continent.children.associate { stat ->
                        stat.key!! to (stat.getValue(Int::class.java) ?: 0)
                    }
                    continent.key!! to continentStats
                }
                quizType.key!! to quizStats
            }
            onComplete(stats)
        }.addOnFailureListener {
            // Handle the error case if needed
            onComplete(emptyMap())
        }
    }

}

