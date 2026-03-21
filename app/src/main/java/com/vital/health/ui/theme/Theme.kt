package com.vital.health.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val dynamicColorScheme get() = if (isAppDarkMode) {
    darkColorScheme(
        primary = PrimaryBlack,
        onPrimary = Color(0xFF1A1714),
        secondary = TanButton,
        onSecondary = TextMain,
        background = CreamBg,
        surface = CreamCard,
        onBackground = TextMain,
        onSurface = TextMain,
        error = VitalError,
        onError = Color.White
    )
} else {
    lightColorScheme(
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
}

@Composable
fun VitalTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = dynamicColorScheme,
        typography = Typography,
        content = content
    )
}
