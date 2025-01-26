package com.example.worldovertest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

@Composable
fun RegisterScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") } // Nouveau champ
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2D))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Créer un compte",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Pseudo", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color(0xFFFFC107),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color(0xFFFFC107),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color(0xFFFFC107),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmez le mot de passe", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color(0xFFFFC107),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFFFFC107))
        } else {
            Button(
                onClick = {
                    if (password == confirmPassword) {
                        if (username.isNotBlank()) {
                            isLoading = true
                            createUser(email, password, username) { success, message ->
                                isLoading = false
                                if (success) {
                                    navController.navigate("login")
                                } else {
                                    errorMessage = message
                                }
                            }
                        } else {
                            errorMessage = "Le pseudo est obligatoire"
                        }
                    } else {
                        errorMessage = "Les mots de passe ne correspondent pas"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFC107)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Créer un compte", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Déjà un compte ? Connectez-vous.", color = Color(0xFFFFC107))
        }

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

private fun createUser(
    email: String,
    password: String,
    username: String,
    onComplete: (Boolean, String?) -> Unit
) {
    Firebase.auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { authResult ->
            val userId = authResult.user?.uid
            if (userId != null) {
                val database = FirebaseDatabase.getInstance("https://worldover-71d92-default-rtdb.europe-west1.firebasedatabase.app").reference
                val userData = mapOf(
                    "email" to email,
                    "username" to username,
                    "stats" to mapOf(
                        "flags" to mapOf(
                            "Asia" to mapOf("correct" to 0, "total" to 0),
                            "Europe" to mapOf("correct" to 0, "total" to 0),
                            "Africa" to mapOf("correct" to 0, "total" to 0),
                            "South America" to mapOf("correct" to 0, "total" to 0),
                            "North America" to mapOf("correct" to 0, "total" to 0),
                            "Oceania" to mapOf("correct" to 0, "total" to 0)
                        ),
                        "capitals" to mapOf(
                            "Asia" to mapOf("correct" to 0, "total" to 0),
                            "Europe" to mapOf("correct" to 0, "total" to 0),
                            "Africa" to mapOf("correct" to 0, "total" to 0),
                            "South America" to mapOf("correct" to 0, "total" to 0),
                            "North America" to mapOf("correct" to 0, "total" to 0),
                            "Oceania" to mapOf("correct" to 0, "total" to 0)
                        )
                    )
                )
                database.child("users").child(userId).setValue(userData)
                    .addOnSuccessListener { onComplete(true, null) }
                    .addOnFailureListener { onComplete(false, "Erreur lors de l'enregistrement : ${it.message}") }
            } else {
                onComplete(false, "Impossible de récupérer l'UID.")
            }
        }
        .addOnFailureListener { error ->
            onComplete(false, "Erreur : ${error.message}")
        }
}
