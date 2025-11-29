package com.example.mentheraap.screens.ui

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mentheraap.data.*
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryDetailScreen(
    userId: String,
    entryId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val journalPrefs = remember { JournalPreferences(context) }
    val scope = rememberCoroutineScope()

    var entry by remember { mutableStateOf<JournalEntry?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    // Cargar entrada
    LaunchedEffect(entryId) {
        scope.launch {
            val entries = journalPrefs.getEntries(userId)
            entry = entries.find { it.id == entryId }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entrada del Diario", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (entry == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text("Entrada no encontrada")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Fecha y hora
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = entry!!.getFormattedDate(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = entry!!.getDayOfWeek(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        // Nivel de ansiedad
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        entry!!.anxietyLevel <= 3 -> Color(0xFF26C281)
                                        entry!!.anxietyLevel <= 6 -> Color(0xFFF39C12)
                                        else -> Color(0xFFE74C3C)
                                    }.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = entry!!.anxietyLevel.toString(),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        entry!!.anxietyLevel <= 3 -> Color(0xFF26C281)
                                        entry!!.anxietyLevel <= 6 -> Color(0xFFF39C12)
                                        else -> Color(0xFFE74C3C)
                                    }
                                )
                                Text(
                                    text = "/10",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                // Emociones
                if (entry!!.emotions.isNotEmpty()) {
                    DetailSection(
                        title = "Emociones",
                        icon = Icons.Default.EmojiEmotions
                    ) {
                        FlowRow(
                            mainAxisSpacing = 8.dp,
                            crossAxisSpacing = 8.dp
                        ) {
                            entry!!.emotions.forEach { emotion ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text("${emotion.emoji} ${emotion.displayName}") }
                                )
                            }
                        }
                    }
                }

                // Situación
                if (entry!!.situation.isNotEmpty()) {
                    DetailSection(
                        title = "¿Qué estaba pasando?",
                        icon = Icons.Default.Event
                    ) {
                        Text(
                            text = entry!!.situation,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f
                        )
                    }
                }

                // Pensamientos
                if (entry!!.thoughts.isNotEmpty()) {
                    DetailSection(
                        title = "Pensamientos",
                        icon = Icons.Default.Psychology
                    ) {
                        Text(
                            text = entry!!.thoughts,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f
                        )
                    }
                }

                // Síntomas físicos
                if (entry!!.physicalSymptoms.isNotEmpty()) {
                    DetailSection(
                        title = "Síntomas físicos",
                        icon = Icons.Default.FavoriteBorder
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            entry!!.physicalSymptoms.forEach { symptom ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Circle,
                                        contentDescription = null,
                                        modifier = Modifier.size(8.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = symptom.displayName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                // Estrategias de afrontamiento
                if (entry!!.copingStrategies.isNotEmpty()) {
                    DetailSection(
                        title = "Estrategias usadas",
                        icon = Icons.Default.SelfImprovement
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            entry!!.copingStrategies.forEach { strategy ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = strategy.emoji,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = strategy.displayName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            // Resultado de estrategias
                            entry!!.wasHelpful?.let { helpful ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (helpful)
                                            Color(0xFF26C281).copy(alpha = 0.1f)
                                        else
                                            Color(0xFFE74C3C).copy(alpha = 0.1f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            if (helpful) Icons.Default.ThumbUp else Icons.Default.ThumbDown,
                                            contentDescription = null,
                                            tint = if (helpful) Color(0xFF26C281) else Color(0xFFE74C3C)
                                        )
                                        Text(
                                            text = if (helpful) "Las estrategias me ayudaron" else "No me ayudaron mucho",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Notas adicionales
                if (entry!!.notes.isNotEmpty()) {
                    DetailSection(
                        title = "Notas adicionales",
                        icon = Icons.Default.Note
                    ) {
                        Text(
                            text = entry!!.notes,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f
                        )
                    }
                }

                // Espaciado final
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialog de confirmación de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text("Eliminar entrada") },
            text = { Text("¿Estás seguro de que deseas eliminar esta entrada? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        isDeleting = true
                        scope.launch {
                            journalPrefs.deleteEntry(entryId)
                            onNavigateBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    enabled = !isDeleting
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onError
                        )
                    } else {
                        Text("Eliminar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    enabled = !isDeleting
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DetailSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            content()
        }
    }
}