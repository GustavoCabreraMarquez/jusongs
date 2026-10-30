package com.swave.swave2000.theme

import androidx.compose.ui.graphics.Color

/** Colors of one time-of-day theme. Values come from `prototypes/modern-time-of-day.html`. */
data class ThemeTokens(
    val bg: Color,
    val ink: Color,
    val ink2: Color,
    val acc: Color,
    val onacc: Color,
    val glass: Color,
    val line: Color,
    val dark: Boolean
)

val MorningTokens = ThemeTokens(
    bg = Color(0xFFFBE6D3),
    ink = Color(0xFF3B2F3A),
    ink2 = Color(0xFF7D6A76),
    acc = Color(0xFFE8895A),
    onacc = Color(0xFFFFFFFF),
    glass = Color(0xFFFFF7EE).copy(alpha = 0.62f),
    line = Color(0xFF3B2F3A).copy(alpha = 0.14f),
    dark = false
)

val NoonTokens = ThemeTokens(
    bg = Color(0xFFFDF0CF),
    ink = Color(0xFF3B2F2A),
    ink2 = Color(0xFF7A6650),
    acc = Color(0xFFE0892E),
    onacc = Color(0xFFFFFFFF),
    glass = Color(0xFFFFFAEC).copy(alpha = 0.66f),
    line = Color(0xFF3B2F2A).copy(alpha = 0.14f),
    dark = false
)

val NightTokens = ThemeTokens(
    bg = Color(0xFF2B2F6E),
    ink = Color(0xFFEEF0FF),
    ink2 = Color(0xFFA9B2DE),
    acc = Color(0xFF8F9BFF),
    onacc = Color(0xFF101640),
    glass = Color(0xFF141A46).copy(alpha = 0.58f),
    line = Color(0xFFEEF0FF).copy(alpha = 0.18f),
    dark = true
)
