package com.example.mentheraap.screens.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mentheraap.data.BreathingExercise
import com.example.mentheraap.data.BreathingExercises
import com.example.mentheraap.data.BreathingPhase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingExerciseScreen(
    exerciseId: String,
    onNavigateBack: () -> Unit
) {
    val exercise = remember { BreathingExercises.getById(exerciseId) }

    if (exercise == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Ejercicio no encontrado")
        }
        return
    }

    var currentPhase by remember { mutableStateOf(BreathingPhase.INHALE) }
    var currentCycle by remember { mutableStateOf(1) }
    var remainingTime by remember { mutableStateOf(exercise.inhaleDuration) }
    var isRunning by remember { mutableStateOf(false) }
    var showInstructions by remember { mutableStateOf(true) }
    var hasCompleted by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Animación del círculo de respiración
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val animatedSize by infiniteTransition.animateFloat(
        initialValue = 120f,
        targetValue = if (currentPhase == BreathingPhase.INHALE) 200f else 120f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (currentPhase) {
                    BreathingPhase.INHALE -> exercise.inhaleDuration * 1000
                    BreathingPhase.EXHALE -> exercise.exhaleDuration * 1000
                    else -> 1000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "size"
    )

    // Lógica del temporizador
    LaunchedEffect(isRunning, currentPhase) {
        if (isRunning && !hasCompleted) {
            while (remainingTime > 0) {
                delay(1000)
                remainingTime--

                if (remainingTime == 0) {
                    // Cambiar de fase
                    when (currentPhase) {
                        BreathingPhase.INHALE -> {
                            if (exercise.holdInDuration > 0) {
                                currentPhase = BreathingPhase.HOLD_IN
                                remainingTime = exercise.holdInDuration
                            } else {
                                currentPhase = BreathingPhase.EXHALE
                                remainingTime = exercise.exhaleDuration
                            }
                        }
                        BreathingPhase.HOLD_IN -> {
                            currentPhase = BreathingPhase.EXHALE
                            remainingTime = exercise.exhaleDuration
                        }
                        BreathingPhase.EXHALE -> {
                            if (exercise.holdOutDuration > 0) {
                                currentPhase = BreathingPhase.HOLD_OUT
                                remainingTime = exercise.holdOutDuration
                            } else {
                                // Completar ciclo
                                if (currentCycle < exercise.cycles) {
                                    currentCycle++
                                    currentPhase = BreathingPhase.INHALE
                                    remainingTime = exercise.inhaleDuration
                                } else {
                                    currentPhase = BreathingPhase.COMPLETE
                                    hasCompleted = true
                                    isRunning = false
                                }
                            }
                        }
                        BreathingPhase.HOLD_OUT -> {
                            if (currentCycle < exercise.cycles) {
                                currentCycle++
                                currentPhase = BreathingPhase.INHALE
                                remainingTime = exercise.inhaleDuration
                            } else {
                                currentPhase = BreathingPhase.COMPLETE
                                hasCompleted = true
                                isRunning = false
                            }
                        }
                        BreathingPhase.COMPLETE -> {
                            isRunning = false
                        }
                    }
                }
            }
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surface
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        exercise.name,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
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
                // Contador de ciclos
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Ciclo $currentCycle de ${exercise.cycles}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Círculo de respiración animado
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier.size(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Círculo animado
                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val currentSize = if (isRunning) animatedSize else 150f
                            val radius = currentSize.dp.toPx()

                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        when (currentPhase) {
                                            BreathingPhase.INHALE -> Color(0xFF7EC4A8)
                                            BreathingPhase.EXHALE -> Color(0xFF87CEEB)
                                            BreathingPhase.HOLD_IN, BreathingPhase.HOLD_OUT -> Color(0xFFB8A4D3)
                                            BreathingPhase.COMPLETE -> Color(0xFFFFB5A0)
                                        }.copy(alpha = 0.7f),
                                        when (currentPhase) {
                                            BreathingPhase.INHALE -> Color(0xFF5FB3A1)
                                            BreathingPhase.EXHALE -> Color(0xFF4FC3F7)
                                            BreathingPhase.HOLD_IN, BreathingPhase.HOLD_OUT -> Color(0xFF9B7FB9)
                                            BreathingPhase.COMPLETE -> Color(0xFFFF8B7A)
                                        }.copy(alpha = 0.9f)
                                    )
                                ),
                                radius = radius,
                                center = center
                            )
                        }

                        // Texto central
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = when (currentPhase) {
                                    BreathingPhase.INHALE -> "Inhala"
                                    BreathingPhase.EXHALE -> "Exhala"
                                    BreathingPhase.HOLD_IN -> "Retén"
                                    BreathingPhase.HOLD_OUT -> "Pausa"
                                    BreathingPhase.COMPLETE -> "¡Completado!"
                                },
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            if (!hasCompleted) {
                                Text(
                                    text = "$remainingTime",
                                    style = MaterialTheme.typography.displayLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            } else {
                                Text(
                                    text = "✨",
                                    style = MaterialTheme.typography.displayLarge
                                )
                            }
                        }
                    }

                    // Instrucción adicional
                    if (isRunning && !hasCompleted) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = when (currentPhase) {
                                BreathingPhase.INHALE -> "Respira profundamente por la nariz"
                                BreathingPhase.EXHALE -> "Suelta el aire lentamente por la boca"
                                BreathingPhase.HOLD_IN -> "Mantén el aire en tus pulmones"
                                BreathingPhase.HOLD_OUT -> "Mantén los pulmones vacíos"
                                BreathingPhase.COMPLETE -> "Has completado el ejercicio"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                // Controles
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "¡Excelente trabajo!",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Has completado ${exercise.cycles} ciclos de ${exercise.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (hasCompleted) {
                            Button(
                                onClick = {
                                    currentPhase = BreathingPhase.INHALE
                                    currentCycle = 1
                                    remainingTime = exercise.inhaleDuration
                                    hasCompleted = false
                                    isRunning = false
                                },
                                modifier = Modifier.weight(1f).height(56.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Repetir")
                            }
                        } else {
                            if (!isRunning) {
                                Button(
                                    onClick = {
                                        isRunning = true
                                        showInstructions = false
                                    },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Comenzar")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        isRunning = false
                                        currentPhase = BreathingPhase.INHALE
                                        currentCycle = 1
                                        remainingTime = exercise.inhaleDuration
                                    },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Detener")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialog de instrucciones inicial
        if (showInstructions) {
            AlertDialog(
                onDismissRequest = { showInstructions = false },
                icon = {
                    Text(
                        text = exercise.icon,
                        style = MaterialTheme.typography.displayMedium
                    )
                },
                title = { Text(exercise.name) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = exercise.description,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        HorizontalDivider()

                        Text(
                            text = "Beneficios:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = exercise.benefits,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        HorizontalDivider()

                        Text(
                            text = "Patrón: ${exercise.inhaleDuration}s inhalar" +
                                    if (exercise.holdInDuration > 0) " - ${exercise.holdInDuration}s retener" else "" +
                                            " - ${exercise.exhaleDuration}s exhalar" +
                                            if (exercise.holdOutDuration > 0) " - ${exercise.holdOutDuration}s pausa" else "",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showInstructions = false
                            isRunning = true
                        }
                    ) {
                        Text("Comenzar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showInstructions = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}