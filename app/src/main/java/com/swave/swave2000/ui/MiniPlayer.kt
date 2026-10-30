package com.swave.swave2000.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.BeveledContainer
import com.swave.swave2000.components.GemBadge
import com.swave.swave2000.components.TactileButton
import com.swave.swave2000.theme.*

@Composable
fun MiniPlayer(ui: PlayerUiState, onOpen: () -> Unit, onToggle: () -> Unit, onNext: () -> Unit, modifier: Modifier = Modifier) {
    BeveledContainer(
        modifier.fillMaxWidth().padding(12.dp),
        cornerRadius = 28.dp,
        backgroundColor = Y2KWarmCream
    ) {
        Row(
            Modifier.clickable(onClick = onOpen).padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GemBadge(size = 30.dp)
            Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                Text(ui.title, fontWeight = FontWeight.Bold, color = Y2KTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(ui.artist, style = MaterialTheme.typography.labelMedium, color = Y2KTextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            TactileButton(onClick = onToggle) {
                Icon(
                    if (ui.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (ui.isPlaying) "Pause" else "Play",
                    tint = Y2KTextPrimary
                )
            }
            TactileButton(onClick = onNext, modifier = Modifier.padding(start = 6.dp)) {
                Icon(Icons.Filled.SkipNext, contentDescription = "Next", tint = Y2KTextPrimary)
            }
        }
    }
}
