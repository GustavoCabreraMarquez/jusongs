package com.swave.swave2000.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MainScreen(vm: PlayerViewModel) {
    val library by vm.library.collectAsStateWithLifecycle()
    val ui by vm.ui.collectAsStateWithLifecycle()

    var tab by rememberSaveable { mutableStateOf(NavTab.Home) }
    var openAlbumId by rememberSaveable { mutableStateOf<Long?>(null) }
    var nowPlaying by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = nowPlaying) { nowPlaying = false }
    BackHandler(enabled = !nowPlaying && openAlbumId != null) { openAlbumId = null }
    // Eject (or the queue ending) returns to the library
    LaunchedEffect(ui.hasQueue) { if (!ui.hasQueue) nowPlaying = false }

    // The painted background is drawn by the caller, so this screen handles the system bar insets itself.
    Box(Modifier.fillMaxSize()) {
        if (nowPlaying && ui.hasQueue) {
            NowPlayingScreen(vm = vm, onClose = { nowPlaying = false }, modifier = Modifier.systemBarsPadding())
        } else {
            Column(Modifier.fillMaxSize()) {
                Column(Modifier.weight(1f).statusBarsPadding()) {
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
                        when (tab) {
                            NavTab.Home -> HomeTab(
                                library = library,
                                onShuffleAll = { vm.playSongs(library.songs.shuffled(), 0); nowPlaying = true },
                                onOpenAlbum = { openAlbumId = it.id },
                                modifier = Modifier.weight(1f),
                                current = ui,
                                onOpenNowPlaying = { nowPlaying = true },
                                onTogglePlay = vm::togglePlay,
                                onRefresh = vm::refreshLibrary
                            )
                            NavTab.Albums -> AlbumsTab(library.albums, onOpen = { openAlbumId = it.id }, modifier = Modifier.weight(1f))
                            NavTab.Songs -> SongsTab(
                                songs = library.songs,
                                currentMediaId = ui.mediaId,
                                onPlay = { index -> vm.playSongs(library.songs, index); nowPlaying = true },
                                modifier = Modifier.weight(1f)
                            )
                        }
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
                BottomNav(selected = tab, onSelect = { tab = it; openAlbumId = null })
            }
        }
    }
}
