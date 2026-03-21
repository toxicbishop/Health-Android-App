package com.vital.health.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlack,
    onPrimary = Color.White,
    secondary = TanButton,
    onSecondary = TextMain,
    background = CreamBg,
    surface = CreamCard,
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
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
