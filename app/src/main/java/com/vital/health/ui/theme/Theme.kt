package com.vital.health.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = PrimaryBlack,
    secondary = DarkSurface,
    onSecondary = TextMain,
    background = DarkBg,
    surface = DarkCard,
    onBackground = TextMain,
    onSurface = TextMain,
    error = VitalError,
    onError = Color.White
)

@Composable
fun VitalTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
