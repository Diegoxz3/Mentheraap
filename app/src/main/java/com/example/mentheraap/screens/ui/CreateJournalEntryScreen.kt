package com.example.mentheraap.screens.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mentheraap.data.*
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJournalEntryScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    onEntrySaved: () -> Unit
) {
    val context = LocalContext.current
    val journalPrefs = remember { JournalPreferences(context) }
    val scope = rememberCoroutineScope()

    var anxietyLevel by remember { mutableStateOf(5) }
    var selectedEmotions by remember { mutableStateOf<Set<Emotion>>(emptySet()) }
    var situation by remember { mutableStateOf("") }
    var thoughts by remember { mutableStateOf("") }
    var selectedSymptoms by remember { mutableStateOf<Set<PhysicalSymptom>>(emptySet()) }
    var selectedStrategies by remember { mutableStateOf<Set<CopingStrategy>>(emptySet()) }
    var notes by remember { mutableStateOf("") }
    var wasHelpful by remember { mutableStateOf<Boolean?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var showEmotionsSheet by remember { mutableStateOf(false) }
    var showSymptomsSheet by remember { mutableStateOf(false) }
    var showStrategiesSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Entrada", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (selectedEmotions.isNotEmpty() && situation.isNotEmpty()) {
                                isSaving = true
                                scope.launch {
                                    val entry = JournalHelper.createEntry(
                                        userId = userId,
                                        anxietyLevel = anxietyLevel,
                                        emotions = selectedEmotions.toList(),
                                        situation = situation,
                                        thoughts = thoughts,
                                        physicalSymptoms = selectedSymptoms.toList(),
                                        copingStrategies = selectedStrategies.toList(),
                                        notes = notes,
                                        wasHelpful = wasHelpful
                                    )
                                    journalPrefs.saveEntry(entry)
                                    onEntrySaved()
                                }
                            }
                        },
                        enabled = selectedEmotions.isNotEmpty() && situation.isNotEmpty() && !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Guardar", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Nivel de ansiedad
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "¿Cuánta ansiedad sientes?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$anxietyLevel/10",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                anxietyLevel <= 3 -> Color(0xFF26C281)
                                anxietyLevel <= 6 -> Color(0xFFF39C12)
                                else -> Color(0xFFE74C3C)
                            }
                        )
                    }

                    Slider(
                        value = anxietyLevel.toFloat(),
                        onValueChange = { anxietyLevel = it.toInt() },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = when {
                                anxietyLevel <= 3 -> Color(0xFF26C281)
                                anxietyLevel <= 6 -> Color(0xFFF39C12)
                                else -> Color(0xFFE74C3C)
                            },
                            activeTrackColor = when {
                                anxietyLevel <= 3 -> Color(0xFF26C281)
                                anxietyLevel <= 6 -> Color(0xFFF39C12)
                                else -> Color(0xFFE74C3C)
                            }
                        )
                    )

                    Text(
                        text = when {
                            anxietyLevel <= 3 -> "😌 Leve - Manejable"
                            anxietyLevel <= 6 -> "😐 Moderada - Presente"
                            anxietyLevel <= 8 -> "😰 Alta - Difícil"
                            else -> "😱 Muy Alta - Abrumadora"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Emociones
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "¿Qué emociones sientes? *",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Puedes seleccionar varias",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        IconButton(onClick = { showEmotionsSheet = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar emoción")
                        }
                    }

                    if (selectedEmotions.isNotEmpty()) {
                        FlowRow(
                            mainAxisSpacing = 8.dp,
                            crossAxisSpacing = 8.dp
                        ) {
                            selectedEmotions.forEach { emotion ->
                                AssistChip(
                                    onClick = { selectedEmotions = selectedEmotions - emotion },
                                    label = { Text("${emotion.emoji} ${emotion.displayName}") },
                                    trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(16.dp)) }
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Toca el + para agregar emociones",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Situación
            OutlinedTextField(
                value = situation,
                onValueChange = { situation = it },
                label = { Text("¿Qué estaba pasando? *") },
                placeholder = { Text("Describe la situación o evento...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Pensamientos
            OutlinedTextField(
                value = thoughts,
                onValueChange = { thoughts = it },
                label = { Text("¿Qué pensabas?") },
                placeholder = { Text("¿Qué pasaba por tu mente?...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Síntomas físicos
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Síntomas físicos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "¿Cómo lo sentiste en tu cuerpo?",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        IconButton(onClick = { showSymptomsSheet = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar síntoma")
                        }
                    }

                    if (selectedSymptoms.isNotEmpty()) {
                        FlowRow(
                            mainAxisSpacing = 8.dp,
                            crossAxisSpacing = 8.dp
                        ) {
                            selectedSymptoms.forEach { symptom ->
                                AssistChip(
                                    onClick = { selectedSymptoms = selectedSymptoms - symptom },
                                    label = { Text(symptom.displayName) },
                                    trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(16.dp)) }
                                )
                            }
                        }
                    }
                }
            }

            // Estrategias de afrontamiento
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "¿Qué hiciste para afrontar?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Estrategias que usaste",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        IconButton(onClick = { showStrategiesSheet = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar estrategia")
                        }
                    }

                    if (selectedStrategies.isNotEmpty()) {
                        FlowRow(
                            mainAxisSpacing = 8.dp,
                            crossAxisSpacing = 8.dp
                        ) {
                            selectedStrategies.forEach { strategy ->
                                AssistChip(
                                    onClick = { selectedStrategies = selectedStrategies - strategy },
                                    label = { Text("${strategy.emoji} ${strategy.displayName}") },
                                    trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(16.dp)) }
                                )
                            }
                        }

                        // Pregunta de efectividad
                        if (wasHelpful == null) {
                            Text(
                                text = "¿Te ayudó?",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { wasHelpful = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.ThumbUp, null, Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Sí")
                                }
                                OutlinedButton(
                                    onClick = { wasHelpful = false },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.ThumbDown, null, Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("No")
                                }
                            }
                        } else {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (wasHelpful == true)
                                        Color(0xFF26C281).copy(alpha = 0.1f)
                                    else
                                        Color(0xFFE74C3C).copy(alpha = 0.1f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { wasHelpful = null }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (wasHelpful == true) "✓ Te ayudó" else "✗ No te ayudó",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Toca para cambiar",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Notas adicionales
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas adicionales") },
                placeholder = { Text("Algo más que quieras registrar...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // Espaciado final
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Bottom Sheet para emociones
    if (showEmotionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEmotionsSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Selecciona tus emociones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Emotion.values().forEach { emotion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedEmotions = if (emotion in selectedEmotions) {
                                    selectedEmotions - emotion
                                } else {
                                    selectedEmotions + emotion
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (emotion in selectedEmotions)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = emotion.emoji,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = emotion.displayName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (emotion in selectedEmotions) {
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }

    // Bottom Sheet para síntomas
    if (showSymptomsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSymptomsSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Síntomas físicos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                PhysicalSymptom.values().forEach { symptom ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedSymptoms = if (symptom in selectedSymptoms) {
                                    selectedSymptoms - symptom
                                } else {
                                    selectedSymptoms + symptom
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (symptom in selectedSymptoms)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = symptom.displayName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (symptom in selectedSymptoms) {
                                Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }

    // Bottom Sheet para estrategias
    if (showStrategiesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showStrategiesSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Estrategias de afrontamiento",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                CopingStrategy.values().forEach { strategy ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedStrategies = if (strategy in selectedStrategies) {
                                    selectedStrategies - strategy
                                } else {
                                    selectedStrategies + strategy
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (strategy in selectedStrategies)
                                MaterialTheme.colorScheme.tertiaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strategy.emoji,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = strategy.displayName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (strategy in selectedStrategies) {
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// Nota: FlowRow viene de Accompanist, agregar al build.gradle:
// implementation("com.google.accompanist:accompanist-flowlayout:0.30.1")