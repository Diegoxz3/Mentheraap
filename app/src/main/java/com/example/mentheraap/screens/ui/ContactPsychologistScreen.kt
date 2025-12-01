package com.example.mentheraap.screens.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.mentheraap.data.RequestUrgency
import com.example.mentheraap.data.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactPsychologistScreen(
    user: User,
    onNavigateBack: () -> Unit,
    onRequestSent: () -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var selectedUrgency by remember { mutableStateOf(RequestUrgency.NORMAL) }
    var showUrgencyDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val requestManager = remember { PsychologistRequestManager() }
    val coroutineScope = rememberCoroutineScope()

    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface
        )
    )

    fun sendRequest() {
        errorMessage = ""

        if (reason.isBlank()) {
            errorMessage = "Por favor describe tu situación"
            return
        }

        if (reason.length < 20) {
            errorMessage = "Por favor proporciona más detalles (mínimo 20 caracteres)"
            return
        }

        isLoading = true
        coroutineScope.launch {
            try {
                val email = "${user.username}@mentheraap.com"
                val result = requestManager.sendRequest(
                    userId = user.id,
                    username = user.username,
                    email = email,
                    reason = reason,
                    urgency = selectedUrgency.name.lowercase()
                )

                result.onSuccess {
                    isLoading = false
                    showSuccessDialog = true
                }.onFailure { error ->
                    isLoading = false
                    errorMessage = "Error al enviar solicitud: ${error.message}"
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Error inesperado: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contactar Psicóloga") },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card informativa
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Comunicación con profesionales",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Una psicóloga profesional revisará tu solicitud y te enviará recomendaciones personalizadas dentro de 24-48 horas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Selector de urgencia
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Nivel de urgencia",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showUrgencyDialog = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        when (selectedUrgency) {
                                            RequestUrgency.LOW -> Icons.Default.TrendingDown
                                            RequestUrgency.NORMAL -> Icons.Default.Remove
                                            RequestUrgency.HIGH -> Icons.Default.Warning
                                        },
                                        contentDescription = null,
                                        tint = when (selectedUrgency) {
                                            RequestUrgency.LOW -> MaterialTheme.colorScheme.tertiary
                                            RequestUrgency.NORMAL -> MaterialTheme.colorScheme.primary
                                            RequestUrgency.HIGH -> MaterialTheme.colorScheme.error
                                        }
                                    )
                                    Text(
                                        text = selectedUrgency.label,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    }
                }

                // Campo de texto para la razón
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Describe tu situación",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        OutlinedTextField(
                            value = reason,
                            onValueChange = {
                                reason = it
                                errorMessage = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            placeholder = {
                                Text(
                                    text = "Cuéntanos qué está pasando...\n\n" +
                                            "Por ejemplo:\n" +
                                            "• ¿Qué síntomas estás experimentando?\n" +
                                            "• ¿Cuándo comenzaron?\n" +
                                            "• ¿Qué situaciones los desencadenan?\n" +
                                            "• ¿Cómo te afectan en tu día a día?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            },
                            enabled = !isLoading
                        )

                        Text(
                            text = "${reason.length} caracteres (mínimo 20)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (reason.length >= 20)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Mensaje de error
                if (errorMessage.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Botón de enviar
                Button(
                    onClick = { sendRequest() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Enviar solicitud",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }

    // Dialog de selección de urgencia
    if (showUrgencyDialog) {
        AlertDialog(
            onDismissRequest = { showUrgencyDialog = false },
            title = { Text("Selecciona el nivel de urgencia") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RequestUrgency.values().forEach { urgency ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedUrgency = urgency
                                    showUrgencyDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedUrgency == urgency)
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
                                Icon(
                                    when (urgency) {
                                        RequestUrgency.LOW -> Icons.Default.TrendingDown
                                        RequestUrgency.NORMAL -> Icons.Default.Remove
                                        RequestUrgency.HIGH -> Icons.Default.Warning
                                    },
                                    contentDescription = null,
                                    tint = when (urgency) {
                                        RequestUrgency.LOW -> MaterialTheme.colorScheme.tertiary
                                        RequestUrgency.NORMAL -> MaterialTheme.colorScheme.primary
                                        RequestUrgency.HIGH -> MaterialTheme.colorScheme.error
                                    }
                                )
                                Text(urgency.label)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showUrgencyDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Dialog de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("¡Solicitud enviada!") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Tu solicitud ha sido enviada exitosamente.")
                    Text(
                        "Una psicóloga profesional la revisará y te enviará recomendaciones personalizadas dentro de 24-48 horas.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Recibirás una notificación cuando haya una respuesta.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onRequestSent()
                    }
                ) {
                    Text("Entendido")
                }
            }
        )
    }
}