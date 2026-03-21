package com.vital.health.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

var isAppDarkMode by mutableStateOf(false)

// Light: warm creams/tans   |   Dark: warm charcoals/espresso
val CreamBg get() = if (isAppDarkMode) Color(0xFF1A1714) else Color(0xFFF5F3EC)
val CreamCard get() = if (isAppDarkMode) Color(0xFF2A2520) else Color(0xFFFAF8F2)
val TanButton get() = if (isAppDarkMode) Color(0xFF3D352C) else Color(0xFFDBD5C4)
val TextMain get() = if (isAppDarkMode) Color(0xFFE8E0D4) else Color(0xFF0D0C0A)
val TextMuted get() = if (isAppDarkMode) Color(0xFF9C9080) else Color(0xFF6B6659)
val PrimaryBlack get() = if (isAppDarkMode) Color(0xFFE8E0D4) else Color(0xFF000000)

val TextDim = Color(0xFF999187)
val VitalError = Color(0xFFC0392B)
val VitalSuccess = Color(0xFF27734A)
val SecondaryTan = Color(0xFFEDEADE)
