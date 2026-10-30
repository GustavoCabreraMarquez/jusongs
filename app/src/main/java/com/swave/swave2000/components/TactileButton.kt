package com.swave.swave2000.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.LocalThemeTokens

/**
 * Flat circular icon button. [solid] = filled `acc` with `onacc` icon (main play/pause);
 * otherwise a `line` outline with an `ink` icon.
 */
@Composable
fun CircleButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    solid: Boolean = false,
    size: Dp = 40.dp,
    iconSize: Dp = if (size >= 60.dp) 28.dp else 20.dp
) {
    val t = LocalThemeTokens.current
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .then(if (solid) Modifier.background(t.acc) else Modifier.border(1.dp, t.line, CircleShape))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription, Modifier.size(iconSize), tint = if (solid) t.onacc else t.ink)
    }
}

/**
 * Flat pill for text actions and toggles. [solid] and [latched] both fill with `acc`;
 * otherwise a `line` outline. Use [ButtonLabel] inside.
 */
@Composable
fun TactileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    solid: Boolean = false,
    latched: Boolean = false,
    content: @Composable () -> Unit
) {
    val t = LocalThemeTokens.current
    val filled = solid || latched
    val shape = RoundedCornerShape(50)
    Box(
        modifier
            .clip(shape)
            .then(if (filled) Modifier.background(t.acc) else Modifier.border(1.dp, t.line, shape))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides if (filled) t.onacc else t.ink, content = content)
    }
}

@Composable
fun ButtonLabel(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier = modifier, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
}
