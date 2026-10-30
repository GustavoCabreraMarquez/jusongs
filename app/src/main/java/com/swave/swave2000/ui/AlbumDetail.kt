package com.swave.swave2000.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.AppIcons
import com.swave.swave2000.components.ArtworkImage
import com.swave.swave2000.components.CircleButton
import com.swave.swave2000.components.glass
import com.swave.swave2000.model.Album
import com.swave.swave2000.model.Song
import com.swave.swave2000.theme.LocalThemeTokens

@Composable
fun AlbumDetail(
    album: Album,
    songs: List<Song>,
    currentMediaId: String?,
    onBack: () -> Unit,
    onPlay: (List<Song>, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val t = LocalThemeTokens.current
    Column(modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            CircleButton(AppIcons.Back, "Back", onBack)
            ArtworkImage(album.artworkUri, Modifier.padding(start = 12.dp).size(64.dp).clip(RoundedCornerShape(14.dp)), album.title)
            Column(Modifier.padding(start = 12.dp)) {
                Text(album.title, style = MaterialTheme.typography.titleMedium, color = t.ink, maxLines = 2)
                Text("${album.artist}  ·  ${plural(album.songCount, "track")}", style = MaterialTheme.typography.bodySmall, color = t.ink2)
            }
        }
        LazyColumn(
            Modifier.weight(1f, fill = false).fillMaxWidth().glass(RoundedCornerShape(20.dp)),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
        ) {
            itemsIndexed(songs, key = { _, s -> s.id }) { index, song ->
                SongRow(song, playing = song.id.toString() == currentMediaId, showArt = false, onClick = { onPlay(songs, index) }, divider = index < songs.lastIndex, scale = 2.0f)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
