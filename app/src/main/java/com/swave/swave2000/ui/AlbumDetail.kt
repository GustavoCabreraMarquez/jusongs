package com.swave.swave2000.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.ArtworkImage
import com.swave.swave2000.components.ButtonLabel
import com.swave.swave2000.components.TactileButton
import com.swave.swave2000.model.Album
import com.swave.swave2000.model.Song
import com.swave.swave2000.theme.*

@Composable
fun AlbumDetail(
    album: Album,
    songs: List<Song>,
    currentMediaId: String?,
    onBack: () -> Unit,
    onPlay: (List<Song>, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TactileButton(onClick = onBack) { ButtonLabel("◀") }
            ArtworkImage(album.artworkUri, Modifier.padding(start = 12.dp).size(64.dp), album.title)
            Column(Modifier.padding(start = 12.dp)) {
                Text(album.title, fontWeight = FontWeight.Bold, color = Y2KTextPrimary, maxLines = 2)
                Text("${album.artist}  ·  ${album.songCount} tracks", color = Y2KTextSecondary)
            }
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 12.dp)) {
            itemsIndexed(songs, key = { _, s -> s.id }) { index, song ->
                SongRow(song, playing = song.id.toString() == currentMediaId, showArt = false, onClick = { onPlay(songs, index) })
                HorizontalDivider(color = Y2KSageGreenDark)
            }
        }
    }
}
