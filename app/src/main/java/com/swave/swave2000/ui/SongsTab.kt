package com.swave.swave2000.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.ArtworkImage
import com.swave.swave2000.components.FormatBadge
import com.swave.swave2000.model.Song
import com.swave.swave2000.theme.*

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
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 12.dp)) {
        itemsIndexed(songs, key = { _, s -> s.id }) { index, song ->
            SongRow(song, playing = song.id.toString() == currentMediaId, showArt = true, onClick = { onPlay(index) })
            HorizontalDivider(color = Y2KSageGreenDark)
        }
    }
}

@Composable
fun SongRow(song: Song, playing: Boolean, showArt: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (showArt) ArtworkImage(song.artworkUri, Modifier.size(44.dp), song.album)
        Column(Modifier.weight(1f)) {
            Text(
                song.title,
                fontWeight = FontWeight.Bold,
                color = if (playing) Y2KSoftTealDark else Y2KTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${song.artist}  ·  ${formatDuration(song.duration)}",
                style = MaterialTheme.typography.labelMedium,
                color = Y2KTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        FormatBadge(song.format, lossless = song.format == "FLAC" || song.format == "WAV")
    }
}
