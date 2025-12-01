package com.example.mentheraap.screens.ui


import java.time.Instant
import java.time.ZoneId
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mentheraap.data.PsychologistRequestManager
import com.example.mentheraap.data.Recommendation
import com.example.mentheraap.data.RecommendationType
import com.example.mentheraap.data.User
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(
    user: User,
    onNavigateBack: () -> Unit,
    onNavigateToExercise: (String) -> Unit
) {
    var recommendations by remember { mutableStateOf<List<Recommendation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedRecommendation by remember { mutableStateOf<Recommendation?>(null) }

    val requestManager = remember { PsychologistRequestManager() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        recommendations = requestManager.getUserRecommendations(user.id)
        isLoading = false
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Recomendaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                recommendations.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Inbox,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tienes recomendaciones aún",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cuando contactes a la psicóloga, recibirás recomendaciones personalizadas aquí.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recommendations) { recommendation ->
                            RecommendationCard(
                                recommendation = recommendation,
                                onClick = { selectedRecommendation = it },
                                onMarkAsRead = {
                                    coroutineScope.launch {
                                        requestManager.markRecommendationAsRead(it.id)
                                        recommendations = recommendations.map { rec ->
                                            if (rec.id == it.id) rec.copy(isRead = true) else rec
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog de detalle de recomendación
    selectedRecommendation?.let { recommendation ->
        RecommendationDetailDialog(
            recommendation = recommendation,
            onDismiss = { selectedRecommendation = null },
            onNavigateToExercise = onNavigateToExercise,
            onMarkAsRead = {
                coroutineScope.launch {
                    requestManager.markRecommendationAsRead(recommendation.id)
                    recommendations = recommendations.map { rec ->
                        if (rec.id == recommendation.id) rec.copy(isRead = true) else rec
                    }
                }
            }
        )
    }
}

@Composable
fun RecommendationCard(
    recommendation: Recommendation,
    onClick: (Recommendation) -> Unit,
    onMarkAsRead: (Recommendation) -> Unit
) {
    val type = RecommendationType.values().find {
        it.name.lowercase() == recommendation.type
    } ?: RecommendationType.ADVICE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(recommendation)
                if (!recommendation.isRead) {
                    onMarkAsRead(recommendation)
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (!recommendation.isRead)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!recommendation.isRead) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = type.emoji,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Column {
                        Text(
                            text = recommendation.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = type.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                if (!recommendation.isRead) {
                    Badge {
                        Text("Nuevo")
                    }
                }
            }

            Text(
                text = recommendation.message.take(100) + if (recommendation.message.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = recommendation.psychologistName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = formatDate(recommendation.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            if (recommendation.exercises.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "${recommendation.exercises.size} ejercicio${if (recommendation.exercises.size > 1) "s" else ""} recomendado${if (recommendation.exercises.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationDetailDialog(
    recommendation: Recommendation,
    onDismiss: () -> Unit,
    onNavigateToExercise: (String) -> Unit,
    onMarkAsRead: () -> Unit
) {
    val type = RecommendationType.values().find {
        it.name.lowercase() == recommendation.type
    } ?: RecommendationType.ADVICE

    LaunchedEffect(Unit) {
        if (!recommendation.isRead) {
            onMarkAsRead()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(
                text = type.emoji,
                style = MaterialTheme.typography.displayMedium
            )
        },
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(recommendation.title)
                Text(
                    text = type.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = recommendation.psychologistName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = formatDate(recommendation.createdAt),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Text(
                    text = recommendation.message,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (recommendation.exercises.isNotEmpty()) {
                    HorizontalDivider()

                    Text(
                        text = "Ejercicios recomendados:",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    recommendation.exercises.forEach { exerciseId ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onNavigateToExercise(exerciseId)
                                    onDismiss()
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (exerciseId.startsWith("breathing"))
                                            Icons.Default.Air
                                        else
                                            Icons.Default.SelfImprovement,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = getExerciseName(exerciseId),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

fun formatDate(dateString: String): String {
    return try {
        // Intentar parsear como timestamp en milisegundos
        val millis = dateString.toLongOrNull()
        if (millis != null) {
            val dateTime = java.time.Instant.ofEpochMilli(millis)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime()

            val now = LocalDateTime.now()
            val daysDifference = java.time.Duration.between(dateTime, now).toDays()

            return when {
                daysDifference == 0L -> "Hoy"
                daysDifference == 1L -> "Ayer"
                daysDifference < 7 -> "Hace $daysDifference días"
                else -> dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            }
        }

        // Si no es timestamp, intentar parsear como ISO
        val dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val now = LocalDateTime.now()
        val daysDifference = java.time.Duration.between(dateTime, now).toDays()

        when {
            daysDifference == 0L -> "Hoy"
            daysDifference == 1L -> "Ayer"
            daysDifference < 7 -> "Hace $daysDifference días"
            else -> dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    } catch (e: Exception) {
        "Recientemente"
    }
}

fun getExerciseName(exerciseId: String): String {
    return when (exerciseId) {
        "breathing_4_7_8" -> "Respiración 4-7-8"
        "breathing_diaphragmatic" -> "Respiración Diafragmática"
        "breathing_box" -> "Box Breathing"
        "breathing_coherence" -> "Coherencia Cardíaca"
        "breathing_emergency" -> "Respiración de Emergencia"
        "breathing_balanced" -> "Respiración Equilibrada"
        "meditation_body_scan" -> "Escaneo Corporal"
        "meditation_thought_observer" -> "Observador de Pensamientos"
        "meditation_self_compassion" -> "Autocompasión"
        "meditation_emergency_anchor" -> "Ancla de Emergencia"
        "meditation_inner_refuge" -> "Refugio Interior"
        "meditation_radical_acceptance" -> "Aceptación Radical"
        else -> exerciseId
    }
}