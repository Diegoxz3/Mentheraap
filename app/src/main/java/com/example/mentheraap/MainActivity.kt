package com.example.mentherap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mentherap.data.User
import com.example.mentherap.ui.theme.MentherapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            var currentUser by remember { mutableStateOf<User?>(null) }

            MentherapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        startDestination = if (currentUser != null) {
                            Screen.Home.route
                        } else {
                            Screen.Login.route
                        },
                        currentUser = currentUser,
                        onUserChanged = { user ->
                            currentUser = user
                        }
                    )
                }
            }
        }
    }
}