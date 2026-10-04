package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkAmber,
    onPrimary = DeepIndigo,
    secondary = DarkTeal,
    onSecondary = DeepIndigo,
    tertiary = DarkGrape,
    onTertiary = DeepIndigo,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkCard,
    onSurface = DarkText,
    surfaceVariant = DarkBorder,
    onSurfaceVariant = DarkMutedText,
    error = DarkCoral,
    onError = DeepIndigo,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = DeepIndigo,
    onPrimary = CardWhite,
    secondary = TealAccent,
    onSecondary = CardWhite,
    tertiary = AmberAccent,
    onTertiary = DeepIndigo,
    background = WarmCream,
    onBackground = DeepIndigo,
    surface = CardWhite,
    onSurface = DeepIndigo,
    surfaceVariant = WarmCream,
    onSurfaceVariant = MutedSecondary,
    error = CoralAccent,
    onError = CardWhite,
    outline = BorderLight
)

@Composable
fun ProblemSolverTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
