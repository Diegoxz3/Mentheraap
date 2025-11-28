package com.example.mentherap.screens.ui

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mentheraap.data.User
import com.example.mentheraap.screens.ui.BreathingExerciseScreen
import com.example.mentheraap.screens.ui.BreathingExercisesScreen
import com.example.mentheraap.screens.ui.HomeScreen
import com.example.mentheraap.screens.ui.LoginScreen
import com.example.mentheraap.screens.ui.RegisterScreen

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object BreathingExercises : Screen("breathing_exercises")
    data object BreathingExercise : Screen("breathing_exercise/{exerciseId}") {
        fun createRoute(exerciseId: String) = "breathing_exercise/$exerciseId"
    }
}

@Composable
fun AppNavigation(
    startDestination: String = Screen.Login.route,
    currentUser: User? = null,
    onUserChanged: (User?) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    val demoUser = User(
                        id = "1",
                        username = "demo",
                        password = "demo123",
                        isAnonymous = false,
                        displayName = "Usuario Demo",
                        avatar = 1
                    )
                    onUserChanged(demoUser)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            currentUser?.let { user ->
                HomeScreen(
                    user = user,
                    onLogout = {
                        onUserChanged(null)
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToBreathing = {
                        navController.navigate(Screen.BreathingExercises.route)
                    }
                )
            }
        }

        composable(Screen.BreathingExercises.route) {
            BreathingExercisesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onExerciseSelected = { exerciseId ->
                    navController.navigate(Screen.BreathingExercise.createRoute(exerciseId))
                }
            )
        }

        composable(
            route = Screen.BreathingExercise.route,
            arguments = listOf(
                navArgument("exerciseId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: return@composable
            BreathingExerciseScreen(
                exerciseId = exerciseId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}