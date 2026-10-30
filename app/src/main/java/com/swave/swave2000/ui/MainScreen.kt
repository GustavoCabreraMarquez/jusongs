package com.swave.swave2000.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swave.swave2000.components.ButtonLabel
import com.swave.swave2000.components.DotMatrixText
import com.swave.swave2000.components.GemBadge
import com.swave.swave2000.components.LcdPanel
import com.swave.swave2000.components.TactileButton
import com.swave.swave2000.theme.*

@Composable
fun MainScreen(vm: PlayerViewModel) {
    val library by vm.library.collectAsStateWithLifecycle()
    val ui by vm.ui.collectAsStateWithLifecycle()

    var tab by rememberSaveable { mutableIntStateOf(0) }
    var openAlbumId by rememberSaveable { mutableStateOf<Long?>(null) }
    var nowPlaying by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = nowPlaying) { nowPlaying = false }
    BackHandler(enabled = !nowPlaying && openAlbumId != null) { openAlbumId = null }
    // Eject (or the queue ending) returns to the library
    LaunchedEffect(ui.hasQueue) { if (!ui.hasQueue) nowPlaying = false }

    Box(Modifier.fillMaxSize().systemBarsPadding()) {
        if (nowPlaying && ui.hasQueue) {
            NowPlayingScreen(vm = vm, onClose = { nowPlaying = false })
        } else {
            Column(Modifier.fillMaxSize()) {
                Header()
                val album = openAlbumId?.let { id -> library.albums.firstOrNull { it.id == id } }
                if (album != null) {
                    AlbumDetail(
                        album = album,
                        songs = library.albumSongs[album.id].orEmpty(),
                        currentMediaId = ui.mediaId,
                        onBack = { openAlbumId = null },
                        onPlay = { songs, index -> vm.playSongs(songs, index); nowPlaying = true },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
                        TactileButton(onClick = { tab = 0 }, latched = tab == 0, modifier = Modifier.weight(1f)) {
                            ButtonLabel("ALBUMS")
                        }
                        Box(Modifier.padding(horizontal = 4.dp))
                        TactileButton(onClick = { tab = 1 }, latched = tab == 1, modifier = Modifier.weight(1f)) {
                            ButtonLabel("SONGS")
                        }
                    }
                    if (tab == 0) {
                        AlbumsTab(library.albums, onOpen = { openAlbumId = it.id }, modifier = Modifier.weight(1f))
                    } else {
                        SongsTab(
                            songs = library.songs,
                            currentMediaId = ui.mediaId,
                            onPlay = { index -> vm.playSongs(library.songs, index); nowPlaying = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (ui.hasQueue) {
                    MiniPlayer(
                        ui = ui,
                        onOpen = { nowPlaying = true },
                        onToggle = vm::togglePlay,
                        onNext = vm::next
                    )
                }
            }
        }
    }
}

@Composable
private fun Header() {
    LcdPanel(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GemBadge(size = 22.dp)
            Box(Modifier.padding(start = 8.dp))
            DotMatrixText("S-WAVE 2000")
        }
    }
}
