package com.swave.swave2000.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.AppIcons
import com.swave.swave2000.components.ArtworkImage
import com.swave.swave2000.components.CircleButton
import com.swave.swave2000.components.glassBlur
import com.swave.swave2000.theme.LocalThemeTokens

/** Glass capsule just above the bottom bar. */
@Composable
fun MiniPlayer(ui: PlayerUiState, onOpen: () -> Unit, onToggle: () -> Unit, onNext: () -> Unit, modifier: Modifier = Modifier) {
    val t = LocalThemeTokens.current
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth().glassBlur(shape)
            .clickable(onClick = onOpen).padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ArtworkImage(ui.artworkUri, Modifier.size(40.dp).clip(RoundedCornerShape(9.dp)), ui.title)
        Column(Modifier.weight(1f)) {
            Text(ui.title, style = MaterialTheme.typography.bodyMedium, color = t.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(ui.artist, style = MaterialTheme.typography.bodySmall, color = t.ink2, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        CircleButton(if (ui.isPlaying) AppIcons.Pause else AppIcons.Play, if (ui.isPlaying) "Pause" else "Play", onToggle)
        CircleButton(AppIcons.Next, "Next", onNext)
    }
}
