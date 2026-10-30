package com.swave.swave2000.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Y2KSoftTeal,
    onPrimary = Y2KTextPrimary,
    secondary = Y2KSageGreen,
    onSecondary = Y2KTextPrimary,
    tertiary = Y2KWarmCream,
    background = Y2KSageGreen,
    surface = Y2KWarmCream,
    onSurface = Y2KTextPrimary,
    surfaceVariant = Y2KWarmCreamDark,
    outline = Y2KSageGreenDark
)

@Composable
fun SWaveTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
