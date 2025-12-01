package com.example.mentheraap.screens.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mentheraap.data.FirebaseAuthManager
import com.example.mentheraap.data.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,  // ⬅️ MODIFICADO: recibe User
    onNavigateToRegister: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val authManager = remember { FirebaseAuthManager() }  // ⬅️ AGREGADO
    val coroutineScope = rememberCoroutineScope()  // ⬅️ AGREGADO

    // ⬅️ NUEVA FUNCIÓN DE LOGIN
    fun validateAndLogin() {
        errorMessage = ""

        when {
            username.isBlank() -> {
                errorMessage = "Por favor ingresa tu nombre de usuario"
            }
            password.isBlank() -> {
                errorMessage = "Por favor ingresa tu contraseña"
            }
            else -> {
                isLoading = true
                coroutineScope.launch {
                    try {
                        // Crear email ficticio (username@mentheraap.com)
                        val email = "$username@mentheraap.com"

                        // Intentar login
                        val result = authManager.loginUser(email, password)

                        result.onSuccess { user ->
                            isLoading = false
                            onLoginSuccess(user)
                        }.onFailure { error ->
                            isLoading = false
                            errorMessage = when {
                                error.message?.contains("network", ignoreCase = true) == true ->
                                    "Error de conexión. Verifica tu internet"
                                error.message?.contains("password", ignoreCase = true) == true ||
                                        error.message?.contains("user", ignoreCase = true) == true ||
                                        error.message?.contains("INVALID", ignoreCase = true) == true ->
                                    "Usuario o contraseña incorrectos"
                                else ->
                                    "Error al iniciar sesión. Verifica tus credenciales"
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

    // Gradiente relajante para el fondo
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface
        )
    )

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
            // Encabezado con logo
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
                text = "Bienvenido de nuevo",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Tarjeta del formulario de login
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
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    // Campo de usuario
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            errorMessage = ""
                        },
                        label = { Text("Nombre de usuario") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    // Campo de contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = "Mostrar contraseña"
                                )
                            }
                        },
                        visualTransformation = if (showPassword)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    // Mensaje de error
                    if (errorMessage.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
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

                    // Botón de inicio de sesión
                    Button(
                        onClick = { validateAndLogin() },  // ⬅️ MODIFICADO
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp  // ⬅️ AGREGADO para mejor visual
                            )
                        } else {
                            Icon(Icons.Default.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Iniciar sesión", style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))  // ⬅️ CAMBIADO de Divider

                    // Botón para ir a registro
                    OutlinedButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear cuenta nueva", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            // Mensaje motivacional
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "\"Un viaje de mil millas comienza con un solo paso\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}