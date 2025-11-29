package com.example.mentheraap.screens.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mentheraap.data.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    onCreateEntry: () -> Unit,
    onEntryClick: (String) -> Unit
) {
    val context = LocalContext.current
    val journalPrefs = remember { JournalPreferences(context) }
    val scope = rememberCoroutineScope()

    var entries by remember { mutableStateOf<List<JournalEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var stats by remember { mutableStateOf<JournalStats?>(null) }

    // Cargar entradas
    LaunchedEffect(Unit) {
        scope.launch {
            entries = journalPrefs.getEntries(userId)
            stats = JournalHelper.calculateStats(entries)
            isLoading = false
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Diario Emocional",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateEntry,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva Entrada") },
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (entries.isEmpty()) {
                // Estado vacío
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "📔",
                        style = MaterialTheme.typography.displayLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tu Diario Está Vacío",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Comienza a registrar tus emociones y descubre patrones que te ayudarán a entender tu ansiedad",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onCreateEntry,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear Mi Primera Entrada")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tarjeta de estadísticas
                    item {
                        stats?.let { s ->
                            StatsCard(stats = s)
                        }
                    }

                    // Encabezado de entradas
                    item {
                        Text(
                            text = "Tus Entradas (${entries.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    // Lista de entradas
                    items(entries) { entry ->
                        JournalEntryCard(
                            entry = entry,
                            onClick = { onEntryClick(entry.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun StatsCard(stats: JournalStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Resumen",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Entradas",
                    value = stats.totalEntries.toString(),
                    icon = "📝"
                )

                StatItem(
                    label = "Ansiedad Promedio",
                    value = String.format("%.1f/10", stats.averageAnxietyLevel),
                    icon = when {
                        stats.averageAnxietyLevel <= 3 -> "😌"
                        stats.averageAnxietyLevel <= 6 -> "😐"
                        else -> "😰"
                    }
                )

                StatItem(
                    label = "Tendencia",
                    value = when (stats.improvementTrend) {
                        "improving" -> "Mejorando"
                        "worsening" -> "Subiendo"
                        else -> "Estable"
                    },
                    icon = when (stats.improvementTrend) {
                        "improving" -> "📈"
                        "worsening" -> "📉"
                        else -> "➡️"
                    }
                )
            }

            if (stats.mostCommonEmotion != null) {
                HorizontalDivider()

                Text(
                    text = "Emoción más frecuente: ${stats.mostCommonEmotion.emoji} ${stats.mostCommonEmotion.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, icon: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = entry.getFormattedDate(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = entry.getDayOfWeek(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Nivel de ansiedad
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                entry.anxietyLevel <= 3 -> Color(0xFF26C281)
                                entry.anxietyLevel <= 6 -> Color(0xFFF39C12)
                                else -> Color(0xFFE74C3C)
                            }.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.anxietyLevel.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            entry.anxietyLevel <= 3 -> Color(0xFF26C281)
                            entry.anxietyLevel <= 6 -> Color(0xFFF39C12)
                            else -> Color(0xFFE74C3C)
                        }
                    )
                }
            }

            // Emociones
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                entry.emotions.take(4).forEach { emotion ->
                    Text(
                        text = emotion.emoji,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (entry.emotions.size > 4) {
                    Text(
                        text = "+${entry.emotions.size - 4}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Vista previa de situación
            if (entry.situation.isNotEmpty()) {
                Text(
                    text = entry.situation,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}