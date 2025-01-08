package com.example.worldover

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object StatsRepository {

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference

    fun saveQuizStats(continent: String, isCorrect: Boolean, quizType: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val statsPath = "users/$userId/stats/$quizType/$continent"

        firebaseDatabase.child(statsPath).get().addOnSuccessListener { snapshot ->
            val currentCorrect = snapshot.child("correct").getValue(Int::class.java) ?: 0
            val currentTotal = snapshot.child("total").getValue(Int::class.java) ?: 0

            val updatedStats = mapOf(
                "correct" to (currentCorrect + if (isCorrect) 1 else 0),
                "total" to (currentTotal + 1)
            )

            firebaseDatabase.child(statsPath).setValue(updatedStats)
                .addOnSuccessListener {
                    println("✅ Statistiques mises à jour avec succès pour $quizType/$continent")
                }
                .addOnFailureListener {
                    println("❌ Échec de la mise à jour des statistiques : ${it.message}")
                }
        }.addOnFailureListener {
            println("❌ Impossible de récupérer les statistiques : ${it.message}")
        }
    }


    fun fetchStats(onStatsFetched: (Map<String, Map<String, Map<String, Int>>>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val statsPath = "users/$userId/stats"

        firebaseDatabase.child(statsPath).get().addOnSuccessListener { snapshot ->
            val result = mutableMapOf<String, Map<String, Map<String, Int>>>()

            snapshot.children.forEach { quizType ->
                val continents = mutableMapOf<String, Map<String, Int>>()

                quizType.children.forEach { continent ->
                    val data = continent.value as? Map<*, *>
                    val correct = (data?.get("correct") as? Long ?: 0).toInt()
                    val total = (data?.get("total") as? Long ?: 0).toInt()

                    continents[continent.key ?: ""] = mapOf(
                        "correct" to correct,
                        "total" to total
                    )
                }

                result[quizType.key ?: ""] = continents
            }

            onStatsFetched(result)
        }.addOnFailureListener {
            println("Erreur lors de la récupération des statistiques : ${it.message}")
        }
    }
}
