package com.example.worldovertest

import android.app.Activity
import com.google.android.gms.common.api.ApiException
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.worldovertest.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showUsernameDialog by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var googleUserId: String? by remember { mutableStateOf(null) }
    var googleAccountEmail by remember { mutableStateOf("") }

    val auth = Firebase.auth
    val context = LocalContext.current as Activity

    // Configure Google Sign-In client
    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
    }

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).getResult(ApiException::class.java)
                handleGoogleSignIn(account, auth) { userId, email, isNewUser ->
                    googleUserId = userId
                    googleAccountEmail = email
                    if (isNewUser) {
                        showUsernameDialog = true
                    } else {
                        navController.navigate("home")
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Échec de la connexion Google : ${e.message}"
                Log.e("LoginScreen", "Erreur Google Sign-In : ${e.message}")
            }
        } else {
            errorMessage = "Connexion Google annulée."
        }
    }

    if (showUsernameDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Choisissez un pseudo") },
            text = {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Pseudo") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (username.isNotBlank()) {
                            initializeUserStats(googleUserId, googleAccountEmail, username)
                            showUsernameDialog = false
                            navController.navigate("home")
                        } else {
                            errorMessage = "Le pseudo est obligatoire."
                        }
                    }
                ) {
                    Text("Confirmer")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2D))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/WorldOver.png")
                .build(),
            contentDescription = "Logo WorldOver",
            modifier = Modifier
                .size(250.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Connexion",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color(0xFFFFC107),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color(0xFFFFC107),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFFFFC107))
        } else {
            Button(
                onClick = {
                    isLoading = true
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { result ->
                            initializeUserStats(result.user?.uid, email, null)
                            isLoading = false
                            navController.navigate("home")
                        }
                        .addOnFailureListener {
                            isLoading = false
                            errorMessage = "Connexion échouée : ${it.message}"
                        }
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFC107)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Se connecter", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) },
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(12.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("file:///android_asset/google.png")
                    .build(),
                contentDescription = "Logo google",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continuer avec Google", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("register") }) {
            Text("Créer un compte", color = Color(0xFFFFC107))
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

private fun handleGoogleSignIn(
    account: GoogleSignInAccount?,
    auth: FirebaseAuth,
    onSuccess: (userId: String?, email: String, isNewUser: Boolean) -> Unit
) {
    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
    auth.signInWithCredential(credential)
        .addOnSuccessListener { result ->
            val userId = result.user?.uid
            val email = account?.email ?: ""

            // Vérification dans la base de données
            val database = FirebaseDatabase.getInstance("https://worldover-71d92-default-rtdb.europe-west1.firebasedatabase.app").reference
            val userRef = database.child("users").child(userId ?: "")

            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // L'utilisateur existe déjà
                    onSuccess(userId, email, false)
                } else {
                    // L'utilisateur est nouveau
                    onSuccess(userId, email, true)
                }
            }
        }
        .addOnFailureListener {
            Log.e("LoginScreen", "Erreur lors de la connexion Google : ${it.message}")
        }
}


private fun initializeUserStats(userId: String?, email: String, username: String?) {
    if (userId == null) return
    val database = FirebaseDatabase.getInstance("https://worldover-71d92-default-rtdb.europe-west1.firebasedatabase.app").reference
    val userRef = database.child("users").child(userId)

    userRef.get().addOnSuccessListener { snapshot ->
        if (!snapshot.exists()) {
            val initialStats = mapOf(
                "email" to email,
                "username" to (username ?: ""),
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
            userRef.setValue(initialStats)
        }
    }
}
