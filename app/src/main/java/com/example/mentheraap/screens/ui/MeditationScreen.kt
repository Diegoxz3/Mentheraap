package com.example.mentheraap.screens.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.mentheraap.data.Meditation
import com.example.mentheraap.data.MeditationStep
import com.example.mentheraap.data.Meditations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(
    meditationId: String,
    onNavigateBack: () -> Unit
) {
    val meditation = remember { Meditations.getById(meditationId) }

    if (meditation == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Meditación no encontrada")
        }
        return
    }

    var currentStepIndex by remember { mutableStateOf(0) }
    var remainingTime by remember { mutableStateOf(meditation.script[0].durationSeconds) }
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var showIntro by remember { mutableStateOf(true) }
    var hasCompleted by remember { mutableStateOf(false) }

    val currentStep = meditation.script.getOrNull(currentStepIndex)
    val totalSteps = meditation.script.size
    val progress = (currentStepIndex + 1).toFloat() / totalSteps.toFloat()

    val scope = rememberCoroutineScope()

    // Animación de ondas de calma
    val infiniteTransition = rememberInfiniteTransition(label = "waves")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Temporizador
    LaunchedEffect(isRunning, isPaused) {
        if (isRunning && !isPaused && !hasCompleted) {
            while (remainingTime > 0) {
                delay(1000)
                remainingTime--

                if (remainingTime == 0) {
                    if (currentStepIndex < totalSteps - 1) {
                        currentStepIndex++
                        remainingTime = meditation.script[currentStepIndex].durationSeconds
                    } else {
                        hasCompleted = true
                        isRunning = false
                    }
                }
            }
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surface
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        meditation.name,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Progreso
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.secondaryContainer,
                        strokeCap = StrokeCap.Round
                    )

                    Text(
                        text = if (hasCompleted)
                            "Meditación completada"
                        else
                            "Paso ${currentStepIndex + 1} de $totalSteps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Visualización central
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!hasCompleted) {
                        // Ondas animadas de fondo
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(200.dp + (index * 50.dp))
                                    .scale(waveScale)
                                    .alpha(waveAlpha / (index + 1))
                                    .background(
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                                        CircleShape
                                    )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            // Icono de la meditación
                            Text(
                                text = meditation.icon,
                                style = MaterialTheme.typography.displayLarge,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Título del paso
                            if (currentStep != null) {
                                Text(
                                    text = currentStep.instruction,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                // Temporizador
                                if (isRunning) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        ),
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            text = formatTime(remainingTime),
                                            style = MaterialTheme.typography.displayMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Instrucción guiada
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                    )
                                ) {
                                    Text(
                                        text = currentStep.voicePrompt,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(20.dp),
                                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f
                                    )
                                }
                            }
                        }
                    } else {
                        // Pantalla de completado
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "✨",
                                style = MaterialTheme.typography.displayLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Meditación Completada",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Has dedicado ${meditation.durationMinutes} minutos a tu bienestar",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Controles
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (hasCompleted) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "¿Cómo te sientes?",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tómate un momento para notar los cambios en tu cuerpo y mente",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                currentStepIndex = 0
                                remainingTime = meditation.script[0].durationSeconds
                                hasCompleted = false
                                isRunning = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Meditar de Nuevo")
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (!isRunning) {
                                Button(
                                    onClick = {
                                        isRunning = true
                                        showIntro = false
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Comenzar")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { isPaused = !isPaused },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (isPaused) "Reanudar" else "Pausar")
                                }

                                OutlinedButton(
                                    onClick = {
                                        if (currentStepIndex < totalSteps - 1) {
                                            currentStepIndex++
                                            remainingTime = meditation.script[currentStepIndex].durationSeconds
                                        }
                                    },
                                    modifier = Modifier.height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    enabled = currentStepIndex < totalSteps - 1
                                ) {
                                    Icon(Icons.Default.SkipNext, contentDescription = "Siguiente")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialog de introducción
        if (showIntro) {
            AlertDialog(
                onDismissRequest = { showIntro = false },
                icon = {
                    Text(
                        text = meditation.icon,
                        style = MaterialTheme.typography.displayMedium
                    )
                },
                title = { Text(meditation.name) },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = meditation.description,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        HorizontalDivider()

                        Text(
                            text = "Beneficios:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = meditation.benefits,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        HorizontalDivider()

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Preparación:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "• Encuentra un lugar tranquilo",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• Ponte cómodo, sentado o acostado",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• Silencia notificaciones",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• Duración: ${meditation.durationMinutes} minutos",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showIntro = false
                            isRunning = true
                        }
                    ) {
                        Text("Comenzar Meditación")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showIntro = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(mins, secs)
}