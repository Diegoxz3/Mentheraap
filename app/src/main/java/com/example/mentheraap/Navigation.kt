package com.example.mentheraap

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mentheraap.data.User
import com.example.mentheraap.screens.ui.BreathingExerciseScreen
import com.example.mentheraap.screens.ui.BreathingExercisesScreen
import com.example.mentheraap.screens.ui.CreateJournalEntryScreen
import com.example.mentherap.screens.ui.HomeScreen
import com.example.mentheraap.screens.ui.JournalEntryDetailScreen
import com.example.mentheraap.screens.ui.JournalScreen
import com.example.mentheraap.screens.ui.LoginScreen
import com.example.mentheraap.screens.ui.MeditationScreen
import com.example.mentheraap.screens.ui.MeditationsScreen
import com.example.mentheraap.screens.ui.RegisterScreen

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object BreathingExercises : Screen("breathing_exercises")
    data object BreathingExercise : Screen("breathing_exercise/{exerciseId}") {
        fun createRoute(exerciseId: String) = "breathing_exercise/$exerciseId"
    }
    data object Meditations : Screen("meditations")
    data object Meditation : Screen("meditation/{meditationId}") {
        fun createRoute(meditationId: String) = "meditation/$meditationId"
    }
    data object Journal : Screen("journal")
    data object CreateJournalEntry : Screen("create_journal_entry")
    data object JournalEntryDetail : Screen("journal_entry/{entryId}") {
        fun createRoute(entryId: String) = "journal_entry/$entryId"
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
                    },
                    onNavigateToMeditation = {
                        navController.navigate(Screen.Meditations.route)
                    },
                    onNavigateToJournal = {
                        navController.navigate(Screen.Journal.route)
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

        composable(Screen.Meditations.route) {
            MeditationsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMeditationSelected = { meditationId ->
                    navController.navigate(Screen.Meditation.createRoute(meditationId))
                }
            )
        }

        composable(
            route = Screen.Meditation.route,
            arguments = listOf(
                navArgument("meditationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val meditationId = backStackEntry.arguments?.getString("meditationId") ?: return@composable
            MeditationScreen(
                meditationId = meditationId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Journal.route) {
            currentUser?.let { user ->
                JournalScreen(
                    userId = user.id,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onCreateEntry = {
                        navController.navigate(Screen.CreateJournalEntry.route)
                    },
                    onEntryClick = { entryId ->
                        navController.navigate(Screen.JournalEntryDetail.createRoute(entryId))
                    }
                )
            }
        }

        composable(Screen.CreateJournalEntry.route) {
            currentUser?.let { user ->
                CreateJournalEntryScreen(
                    userId = user.id,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onEntrySaved = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            route = Screen.JournalEntryDetail.route,
            arguments = listOf(
                navArgument("entryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId") ?: return@composable
            currentUser?.let { user ->
                JournalEntryDetailScreen(
                    userId = user.id,
                    entryId = entryId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}