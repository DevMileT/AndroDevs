package com.androdevs.luckysevens.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Esquema único de Lucky Sevens: siempre oscuro, sin colores dinámicos.
private val LuckySevensColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = BackgroundDark,
    secondary = AccentPurple,
    onSecondary = TextLight,
    background = BackgroundDark,
    onBackground = TextLight,
    surface = SurfaceDark,
    onSurface = TextLight
)

@Composable
fun LuckySevensTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LuckySevensColorScheme,
        typography = Typography,
        content = content
    )
}