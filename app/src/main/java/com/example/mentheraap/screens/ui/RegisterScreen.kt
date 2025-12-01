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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mentheraap.data.Avatars
import com.example.mentheraap.data.FirebaseAuthManager
import com.example.mentheraap.data.User
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (User) -> Unit,  // ⬅️ MODIFICADO: recibe User
    onNavigateToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf(1) }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showAvatarPicker by remember { mutableStateOf(false) }

    val authManager = remember { FirebaseAuthManager() }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface
        )
    )

    // ⬅️ NUEVA FUNCIÓN DE VALIDACIÓN Y REGISTRO
    fun validateAndRegister() {
        errorMessage = ""

        when {
            username.isBlank() -> errorMessage = "El nombre de usuario es requerido"
            username.length < 3 -> errorMessage = "El usuario debe tener al menos 3 caracteres"
            password.isBlank() -> errorMessage = "La contraseña es requerida"
            password.length < 6 -> errorMessage = "La contraseña debe tener al menos 6 caracteres"
            password != confirmPassword -> errorMessage = "Las contraseñas no coinciden"
            !isAnonymous && displayName.isBlank() -> errorMessage = "Por favor ingresa tu nombre"
            else -> {
                // Registrar con Firebase
                isLoading = true
                coroutineScope.launch {
                    try {
                        // Verificar si el username ya existe
                        val exists = authManager.usernameExists(username)
                        if (exists) {
                            errorMessage = "Este nombre de usuario ya está en uso"
                            isLoading = false
                            return@launch
                        }

                        // Crear email ficticio (username@mentheraap.com)
                        val email = "$username@mentheraap.com"

                        // Registrar usuario
                        val result = authManager.registerUser(
                            email = email,
                            password = password,
                            username = username,
                            isAnonymous = isAnonymous,
                            displayName = if (isAnonymous) "Anónimo" else displayName,
                            avatar = selectedAvatar
                        )

                        result.onSuccess { user ->
                            isLoading = false
                            onRegisterSuccess(user)
                        }.onFailure { error ->
                            isLoading = false
                            errorMessage = when {
                                error.message?.contains("network", ignoreCase = true) == true ->
                                    "Error de conexión. Verifica tu internet"
                                error.message?.contains("email", ignoreCase = true) == true ->
                                    "Error con el correo electrónico"
                                else ->
                                    "Error al registrar: ${error.message}"
                            }
                        }
                    } catch (e: Exception) {
                        isLoading = false
                        errorMessage = "Error inesperado: ${e.message}"
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🌱",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Mentheraap",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Tu espacio de bienestar",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Crear cuenta",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            errorMessage = ""
                        },
                        label = { Text("Nombre de usuario") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading  // ⬅️ AGREGADO
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Mostrar contraseña"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading  // ⬅️ AGREGADO
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorMessage = ""
                        },
                        label = { Text("Confirmar contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading  // ⬅️ AGREGADO
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = !isLoading) { isAnonymous = !isAnonymous }  // ⬅️ MODIFICADO
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Modo anónimo",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = "Usa un avatar en lugar de tu nombre real",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Switch(
                            checked = isAnonymous,
                            onCheckedChange = { isAnonymous = it },
                            enabled = !isLoading  // ⬅️ AGREGADO
                        )
                    }

                    if (isAnonymous) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Elige tu avatar",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )

                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isLoading) { showAvatarPicker = true },  // ⬅️ MODIFICADO
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
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
                                        Text(
                                            text = Avatars.getAvatar(selectedAvatar),
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                        Text(
                                            text = Avatars.getAvatarName(selectedAvatar),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                    Icon(Icons.Default.Edit, contentDescription = "Cambiar avatar")
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = {
                                displayName = it
                                errorMessage = ""
                            },
                            label = { Text("Tu nombre") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("¿Cómo te llamas?") },
                            enabled = !isLoading  // ⬅️ AGREGADO
                        )
                    }

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Button(
                        onClick = { validateAndRegister() },  // ⬅️ MODIFICADO
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading  // ⬅️ AGREGADO
                    ) {
                        if (isLoading) {  // ⬅️ AGREGADO
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Crear cuenta", style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    TextButton(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading  // ⬅️ AGREGADO
                    ) {
                        Text("¿Ya tienes cuenta? Inicia sesión")
                    }
                }
            }
        }
    }

    if (showAvatarPicker) {
        AlertDialog(
            onDismissRequest = { showAvatarPicker = false },
            title = { Text("Elige tu avatar") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Avatars.list.forEachIndexed { index, avatar ->
                        val (emoji, name) = avatar
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedAvatar = index + 1
                                    showAvatarPicker = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedAvatar == index + 1)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = emoji, style = MaterialTheme.typography.headlineMedium)
                                Text(text = name, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarPicker = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}