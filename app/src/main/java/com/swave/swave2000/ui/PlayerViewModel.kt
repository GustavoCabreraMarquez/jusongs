package com.swave.swave2000.ui

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.swave.swave2000.audio.AudioBus
import com.swave.swave2000.data.FormatInfoReader
import com.swave.swave2000.data.Library
import com.swave.swave2000.data.MediaRepository
import com.swave.swave2000.data.TrackFormat
import com.swave.swave2000.model.Song
import com.swave.swave2000.service.EffectsCommand
import com.swave.swave2000.service.PlaybackService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

enum class BandMode(val label: String) {
    Off("REPEAT OFF"), All("REPEAT ALL"), One("REPEAT ONE"), Shuffle("SHUFFLE");

    fun next() = entries[(ordinal + 1) % entries.size]
}

data class PlayerUiState(
    val hasQueue: Boolean = false,
    val mediaId: String? = null,
    val title: String = "",
    val artist: String = "",
    val artworkUri: Uri? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val bandMode: BandMode = BandMode.Off,
    val bassOn: Boolean = false,
    val virtualizerOn: Boolean = false,
    val format: TrackFormat? = null
)

@OptIn(UnstableApi::class)
class PlayerViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = MediaRepository(app)
    private val audioManager = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _library = MutableStateFlow(Library.EMPTY)
    val library: StateFlow<Library> = _library.asStateFlow()

    private val _ui = MutableStateFlow(PlayerUiState())
    val ui: StateFlow<PlayerUiState> = _ui.asStateFlow()

    private val _volume = MutableStateFlow(readVolume())
    val volume: StateFlow<Float> = _volume.asStateFlow()

    val spectrum: StateFlow<FloatArray> = AudioBus.spectrum

    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var libraryJob: Job? = null
    private var formatJob: Job? = null
    private var formatFor: String? = null

    private val volumeObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            _volume.value = readVolume()
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) = sync()
    }

    private val controllerListener = object : MediaController.Listener {
        override fun onExtrasChanged(controller: MediaController, extras: Bundle) = applyExtras(extras)
    }

    init {
        app.contentResolver.registerContentObserver(Settings.System.CONTENT_URI, true, volumeObserver)
        connect(app)
        viewModelScope.launch {
            while (true) {
                controller?.takeIf { it.isPlaying }?.let { c ->
                    _ui.update { it.copy(positionMs = c.currentPosition.coerceAtLeast(0)) }
                }
                delay(250)
            }
        }
    }

    private fun connect(context: Context) {
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, token).setListener(controllerListener).buildAsync()
        controllerFuture = future
        future.addListener({
            val c = runCatching { future.get() }.getOrNull() ?: return@addListener
            controller = c
            c.addListener(playerListener)
            applyExtras(c.sessionExtras)
            sync()
        }, ContextCompat.getMainExecutor(context))
    }

    /** Starts watching MediaStore. Call once the audio permission is granted. */
    fun startLibrary() {
        if (libraryJob?.isActive == true) return
        repository.ensureLibraryFolder()
        libraryJob = viewModelScope.launch { repository.libraryFlow().collect { _library.value = it } }
    }

    /** Re-indexes the library folder and reloads the library. */
    fun refreshLibrary() {
        viewModelScope.launch { _library.value = repository.rescan() }
    }

    // ---- Playback -------------------------------------------------------------------------

    fun playSongs(songs: List<Song>, startIndex: Int) {
        val c = controller ?: return
        c.setMediaItems(songs.map(::toMediaItem), startIndex, 0)
        c.prepare()
        c.play()
    }

    fun togglePlay() {
        val c = controller ?: return
        if (c.isPlaying) c.pause() else c.play()
    }

    fun next() { controller?.seekToNext() }
    fun previous() { controller?.seekToPrevious() }

    /** Stops playback and clears the queue. */
    fun eject() {
        controller?.run {
            stop()
            clearMediaItems()
        }
    }

    fun cycleBand() {
        val c = controller ?: return
        when (_ui.value.bandMode.next()) {
            BandMode.Off -> { c.repeatMode = Player.REPEAT_MODE_OFF; c.shuffleModeEnabled = false }
            BandMode.All -> { c.repeatMode = Player.REPEAT_MODE_ALL; c.shuffleModeEnabled = false }
            BandMode.One -> { c.repeatMode = Player.REPEAT_MODE_ONE; c.shuffleModeEnabled = false }
            BandMode.Shuffle -> { c.repeatMode = Player.REPEAT_MODE_ALL; c.shuffleModeEnabled = true }
        }
    }

    fun toggleBass() = sendEffects(!_ui.value.bassOn, _ui.value.virtualizerOn)
    fun toggleVirtualizer() = sendEffects(_ui.value.bassOn, !_ui.value.virtualizerOn)

    private fun sendEffects(bass: Boolean, virtualizer: Boolean) {
        _ui.update { it.copy(bassOn = bass, virtualizerOn = virtualizer) }
        controller?.sendCustomCommand(EffectsCommand.command, EffectsCommand.bundle(bass, virtualizer))
    }

    // ---- Volume ---------------------------------------------------------------------------

    /** Drives the system music stream so the hardware keys stay in sync. */
    fun setVolume(fraction: Float) {
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (fraction * max).roundToInt(), 0)
        _volume.value = readVolume()
    }

    private fun readVolume(): Float {
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / max
    }

    // ---- State sync -----------------------------------------------------------------------

    private fun applyExtras(extras: Bundle) {
        _ui.update {
            it.copy(
                bassOn = extras.getBoolean(EffectsCommand.KEY_BASS),
                virtualizerOn = extras.getBoolean(EffectsCommand.KEY_VIRTUALIZER)
            )
        }
    }

    private fun sync() {
        val c = controller ?: return
        val item = c.currentMediaItem
        val mode = when {
            c.shuffleModeEnabled -> BandMode.Shuffle
            c.repeatMode == Player.REPEAT_MODE_ONE -> BandMode.One
            c.repeatMode == Player.REPEAT_MODE_ALL -> BandMode.All
            else -> BandMode.Off
        }
        _ui.update {
            it.copy(
                hasQueue = item != null,
                mediaId = item?.mediaId,
                title = item?.mediaMetadata?.title?.toString().orEmpty(),
                artist = item?.mediaMetadata?.artist?.toString().orEmpty(),
                artworkUri = item?.mediaMetadata?.artworkUri,
                isPlaying = c.isPlaying,
                positionMs = c.currentPosition.coerceAtLeast(0),
                durationMs = c.duration.takeIf { d -> d != C.TIME_UNSET } ?: 0,
                bandMode = mode,
                format = if (item?.mediaId == formatFor) it.format else null
            )
        }
        loadFormat(item)
    }

    /** Reads the format badge for the current track only, so library scans stay fast. */
    private fun loadFormat(item: MediaItem?) {
        val id = item?.mediaId
        if (id == formatFor) return
        formatFor = id
        formatJob?.cancel()
        val uri = item?.localConfiguration?.uri ?: return
        formatJob = viewModelScope.launch {
            val format = withContext(Dispatchers.IO) { FormatInfoReader.read(getApplication(), uri) }
            if (formatFor == id) _ui.update { it.copy(format = format) }
        }
    }

    private fun toMediaItem(song: Song) = MediaItem.Builder()
        .setMediaId(song.id.toString())
        .setUri(song.contentUri)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .setArtworkUri(song.artworkUri)
                .build()
        )
        .build()

    override fun onCleared() {
        getApplication<Application>().contentResolver.unregisterContentObserver(volumeObserver)
        controller?.removeListener(playerListener)
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controller = null
        super.onCleared()
    }
}
