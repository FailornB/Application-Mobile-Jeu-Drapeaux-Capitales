package com.example.worldover


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.worldover.ui.theme.QuizAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
