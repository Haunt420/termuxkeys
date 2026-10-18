package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MatrixGreenBright,
    onPrimary = TerminalBg,
    primaryContainer = MatrixGreenDark,
    onPrimaryContainer = MatrixGreenGlow,
    secondary = CyberCyan,
    onSecondary = TerminalBg,
    secondaryContainer = CyberCyanMuted,
    onSecondaryContainer = TextPrimary,
    tertiary = AmberAccent,
    background = TerminalBg,
    onBackground = TextPrimary,
    surface = TerminalSurface,
    onSurface = TextPrimary,
    surfaceVariant = TerminalSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = TerminalBorder,
    error = RedAccent
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Termux is inherently a dark terminal application; always default to high-contrast cyberpunk dark palette
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
