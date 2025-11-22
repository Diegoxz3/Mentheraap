package com.example.mentherap.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Esquema de colores para modo claro - Tonos relajantes
 */
private val LightColorScheme = lightColorScheme(
    primary = MintGreen,
    onPrimary = Color.White,
    primaryContainer = LightBlue,
    onPrimaryContainer = DarkGray,

    secondary = Lavender,
    onSecondary = Color.White,
    secondaryContainer = LightLavender,
    onSecondaryContainer = DarkGray,

    tertiary = Peach,
    onTertiary = Color.White,
    tertiaryContainer = LightPeach,
    onTertiaryContainer = DarkGray,

    background = Cream,
    onBackground = DarkGray,
    surface = Color.White,
    onSurface = DarkGray,
    surfaceVariant = LightGray,
    onSurfaceVariant = MediumGray,

    error = Coral,
    onError = Color.White,
    errorContainer = LightPeach,
    onErrorContainer = DarkGray
)

/**
 * Esquema de colores para modo oscuro - Tonos suaves
 */
private val DarkColorScheme = darkColorScheme(
    primary = SoftTeal,
    onPrimary = Color.White,
    primaryContainer = DeepTeal,
    onPrimaryContainer = LightBlue,

    secondary = LightLavender,
    onSecondary = DarkGray,
    secondaryContainer = DeepLavender,
    onSecondaryContainer = LightLavender,

    tertiary = LightPeach,
    onTertiary = DarkGray,
    tertiaryContainer = Coral,
    onTertiaryContainer = LightPeach,

    background = DarkBackground,
    onBackground = Cream,
    surface = DarkSurface,
    onSurface = Cream,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = LightGray,

    error = Coral,
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = LightPeach
)

@Composable
fun MentherapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}