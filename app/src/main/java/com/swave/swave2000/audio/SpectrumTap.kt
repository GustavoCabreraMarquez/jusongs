package com.swave.swave2000.audio

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Process-wide hand-off between the playback service (producer) and the UI (consumer). */
object AudioBus {
    val spectrum = MutableStateFlow(FloatArray(SpectrumMath.BANDS))
}

/**
 * Passes PCM through untouched while collecting mono samples for a spectrum display.
 * The FFT runs on a background dispatcher; the audio thread only copies samples.
 * Needs no RECORD_AUDIO permission, unlike android.media.audiofx.Visualizer.
 */
@UnstableApi
class SpectrumTap(private val output: MutableStateFlow<FloatArray>) : BaseAudioProcessor() {

    private class Block(val samples: FloatArray, val sampleRate: Int)

    private val window = FloatArray(SpectrumMath.FFT_SIZE)
    private var filled = 0
    private var sampleRate = 44100
    private var channels = 2
    private var float = false

    private val blocks = Channel<Block>(Channel.CONFLATED)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch {
            for (block in blocks) output.value = SpectrumMath.bands(block.samples, block.sampleRate)
        }
    }

    override fun onConfigure(inputAudioFormat: AudioFormat): AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT && inputAudioFormat.encoding != C.ENCODING_PCM_FLOAT) {
            return AudioFormat.NOT_SET // inactive: audio flows past untouched
        }
        sampleRate = inputAudioFormat.sampleRate
        channels = inputAudioFormat.channelCount
        float = inputAudioFormat.encoding == C.ENCODING_PCM_FLOAT
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val size = inputBuffer.remaining()
        if (size == 0) return
        collect(inputBuffer.duplicate().order(ByteOrder.nativeOrder()))
        replaceOutputBuffer(size).put(inputBuffer).flip()
    }

    private fun collect(buf: ByteBuffer) {
        val bytesPerSample = if (float) 4 else 2
        val frameBytes = bytesPerSample * channels
        while (buf.remaining() >= frameBytes) {
            var sum = 0f
            repeat(channels) { sum += if (float) buf.getFloat() else buf.getShort() / 32768f }
            window[filled++] = sum / channels
            if (filled == window.size) {
                blocks.trySend(Block(window.copyOf(), sampleRate))
                filled = 0
            }
        }
    }

    override fun onFlush() {
        filled = 0
    }

    override fun onReset() {
        filled = 0
    }

    fun release() {
        scope.cancel()
    }
}
