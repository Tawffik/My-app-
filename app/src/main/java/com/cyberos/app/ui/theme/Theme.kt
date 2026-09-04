package com.cyberos.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberColors = darkColorScheme(
    primary = CyberAccent,
    onPrimary = CyberAccentOn,
    primaryContainer = CyberAccentContainer,
    onPrimaryContainer = CyberAccentText,
    secondary = CyberAccentText,
    onSecondary = CyberAccentOn,
    background = CyberBackground,
    onBackground = CyberText,
    surface = CyberSurface,
    onSurface = CyberText,
    surfaceVariant = CyberSurfaceRaised,
    onSurfaceVariant = CyberTextMuted,
    outline = CyberBorder,
    error = CyberDanger,
    onError = CyberBackground,
    errorContainer = CyberDangerContainer,
    onErrorContainer = CyberDangerText
)

@Composable
fun CyberTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CyberColors, content = content)
}
