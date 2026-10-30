package com.swave.swave2000.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.ArtworkImage
import com.swave.swave2000.components.FormatBadge
import com.swave.swave2000.components.glass
import com.swave.swave2000.model.Song
import com.swave.swave2000.theme.LocalThemeTokens

@Composable
fun SongsTab(
    songs: List<Song>,
    currentMediaId: String?,
    onPlay: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (songs.isEmpty()) {
        EmptyLibrary(modifier)
        return
    }
    Column(modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ScreenTitle("Songs", plural(songs.size, "track"))
        // Rows sit on a glass panel so they stay readable over any part of the scenery.
        LazyColumn(
            Modifier.weight(1f, fill = false).fillMaxWidth().glass(RoundedCornerShape(20.dp)),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
        ) {
            itemsIndexed(songs, key = { _, s -> s.id }) { index, song ->
                SongRow(song, playing = song.id.toString() == currentMediaId, showArt = true, onClick = { onPlay(index) }, divider = index < songs.lastIndex)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun SongRow(song: Song, playing: Boolean, showArt: Boolean, onClick: () -> Unit, divider: Boolean = true, scale: Float = 1f) {
    val t = LocalThemeTokens.current
    Column {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = (8 * scale).dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showArt) ArtworkImage(song.artworkUri, Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)), song.album)
            Column(Modifier.weight(1f)) {
                Text(
                    song.title,
                    style = MaterialTheme.typography.bodyMedium.let { if (scale == 1f) it else it.copy(fontSize = it.fontSize * scale, lineHeight = it.lineHeight * scale) },
                    color = if (playing) t.acc else t.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${song.artist}  ·  ${formatDuration(song.duration)}",
                    style = MaterialTheme.typography.bodySmall.let { if (scale == 1f) it else it.copy(fontSize = it.fontSize * scale, lineHeight = it.lineHeight * scale) },
                    color = t.ink2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            FormatBadge(song.format)
        }
        if (divider) HorizontalDivider(color = t.line)
    }
}
