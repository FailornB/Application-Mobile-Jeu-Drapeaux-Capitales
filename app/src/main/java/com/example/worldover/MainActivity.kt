/*package com.example.worldover


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.worldover.ui.theme.QuizAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setContent {
            QuizAppTheme {
                val navController = rememberNavController()
                val context = applicationContext
                AppNavigation(navController = navController, context = context)
            }
        }*/

        setContent {
            QuizAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}*/
package com.example.worldover

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.worldover.ui.theme.QuizAppTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            QuizAppTheme {
                val navController = rememberNavController()
                val context = applicationContext
                AppNavigation(navController = navController, context = context)
            }
        }
    }
}
