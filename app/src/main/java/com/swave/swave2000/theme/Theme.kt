package com.swave.swave2000.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import java.time.LocalTime

/** Look chosen from the local time at which the app is opened. */
enum class AppTheme(val tokens: ThemeTokens) {
    Morning(MorningTokens),
    Noon(NoonTokens),
    Night(NightTokens);

    companion object {
        /** 05:00-09:59 morning, 10:00-17:59 noon, 18:00-04:59 night. */
        fun at(time: LocalTime): AppTheme = when (time.hour) {
            in 5..9 -> Morning
            in 10..17 -> Noon
            else -> Night
        }
    }
}

val LocalThemeTokens = staticCompositionLocalOf { MorningTokens }
val LocalAppTheme = staticCompositionLocalOf { AppTheme.Morning }

@Composable
fun SWaveTheme(theme: AppTheme, content: @Composable () -> Unit) {
    val t = theme.tokens
    val scheme = if (t.dark) {
        darkColorScheme(primary = t.acc, onPrimary = t.onacc, background = t.bg, surface = t.bg, onSurface = t.ink, onBackground = t.ink, outline = t.line)
    } else {
        lightColorScheme(primary = t.acc, onPrimary = t.onacc, background = t.bg, surface = t.bg, onSurface = t.ink, onBackground = t.ink, outline = t.line)
    }
    CompositionLocalProvider(
        LocalThemeTokens provides t,
        LocalAppTheme provides theme,
        LocalContentColor provides t.ink
    ) {
        MaterialTheme(colorScheme = scheme, typography = Typography, content = content)
    }
}
