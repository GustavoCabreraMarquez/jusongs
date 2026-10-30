package com.swave.swave2000.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.*

@Composable
fun TactileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Y2KSoftTeal,
    latched: Boolean = false,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value

    // When pressed (or latched on), we swap from Extruded to Recessed to simulate the button being pushed in
    val down = isPressed || latched
    BeveledContainer(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        type = if (down) BevelType.Recessed else BevelType.Extruded,
        backgroundColor = when {
            latched -> Y2KSoftTealDark
            isPressed -> backgroundColor.copy(alpha = 0.9f)
            else -> backgroundColor
        },
        cornerRadius = 4.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun ButtonLabel(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier = modifier, style = LcdSubTextStyle.copy(color = Y2KTextPrimary, fontWeight = FontWeight.Bold))
}
