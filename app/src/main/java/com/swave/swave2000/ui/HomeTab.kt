package com.swave.swave2000.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.ArtworkImage
import com.swave.swave2000.components.ButtonLabel
import com.swave.swave2000.components.TactileButton
import com.swave.swave2000.data.Library
import com.swave.swave2000.model.Album
import com.swave.swave2000.theme.*

@Composable
fun HomeTab(
    library: Library,
    onShuffleAll: () -> Unit,
    onOpenAlbum: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    if (library.songs.isEmpty()) {
        EmptyLibrary(modifier)
        return
    }
    Column(modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "${library.songs.size} songs  ·  ${library.albums.size} albums",
            color = Y2KTextSecondary
        )
        TactileButton(onClick = onShuffleAll, modifier = Modifier.fillMaxWidth()) {
            ButtonLabel("SHUFFLE ALL")
        }
        Text("Your albums", fontWeight = FontWeight.Bold, color = Y2KTextPrimary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(0.dp)) {
            items(library.albums, key = { it.id }) { album ->
                Column(Modifier.width(120.dp).clickable { onOpenAlbum(album) }) {
                    ArtworkImage(album.artworkUri, Modifier.size(120.dp), album.title)
                    Text(
                        album.title,
                        fontWeight = FontWeight.Bold,
                        color = Y2KTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

private fun Modifier.fillMaxWidth() = this.then(Modifier.fillMaxWidth())
