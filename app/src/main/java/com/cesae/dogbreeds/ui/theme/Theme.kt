package com.cesae.dogbreeds.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF8B4513),
    onPrimary = Color.White,
    secondary = Color(0xFFD2691E),
    onSecondary = Color.White,
    background = Color(0xFFFFFBF5),
    surface = Color.White,
)

@Composable
fun DogBreedsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
