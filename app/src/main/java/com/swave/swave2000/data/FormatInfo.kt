package com.swave.swave2000.data

import android.content.Context
import android.media.AudioFormat
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri

/** Technical details of one track, used for the badge on the Now Playing screen. */
data class TrackFormat(
    val codec: String,
    val bitDepth: Int?,
    val sampleRateHz: Int?,
    val bitrateKbps: Int?,
    val lossless: Boolean
) {
    /** For example `FLAC 24-bit 96kHz` or `MP3 320kbps 44.1kHz`. */
    fun label(): String = buildString {
        append(codec)
        if (lossless && bitDepth != null) append(" ").append(bitDepth).append("-bit")
        else if (!lossless && bitrateKbps != null) append(" ").append(bitrateKbps).append("kbps")
        sampleRateHz?.let { append(" ").append(formatKhz(it)) }
    }

    companion object {
        fun formatKhz(hz: Int): String {
            val khz = hz / 1000.0
            return if (hz % 1000 == 0) "${hz / 1000}kHz" else "${"%.1f".format(java.util.Locale.US, khz)}kHz"
        }
    }
}

object FormatInfoReader {

    /** Reads the header of a single file. Called lazily, for the current track only. */
    fun read(context: Context, uri: Uri): TrackFormat? = runCatching {
        val extractor = MediaExtractor()
        try {
            extractor.setDataSource(context, uri, null)
            val index = (0 until extractor.trackCount).firstOrNull {
                extractor.getTrackFormat(it).getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true
            } ?: return null
            val f = extractor.getTrackFormat(index)
            val mime = f.getString(MediaFormat.KEY_MIME).orEmpty()
            val (codec, lossless) = codecOf(mime)
            TrackFormat(
                codec = codec,
                bitDepth = bitDepthOf(f),
                sampleRateHz = f.intOrNull(MediaFormat.KEY_SAMPLE_RATE),
                bitrateKbps = (f.intOrNull(MediaFormat.KEY_BIT_RATE) ?: estimateBitrate(context, uri, f))
                    ?.let { (it + 500) / 1000 },
                lossless = lossless
            )
        } finally {
            extractor.release()
        }
    }.getOrNull()

    private fun codecOf(mime: String): Pair<String, Boolean> = when {
        mime.endsWith("flac") -> "FLAC" to true
        mime.endsWith("alac") -> "ALAC" to true
        mime.endsWith("raw") || mime.contains("wav") || mime.contains("pcm") -> "WAV" to true
        mime.endsWith("mpeg") -> "MP3" to false
        else -> "M4A" to false
    }

    private fun bitDepthOf(f: MediaFormat): Int? {
        f.intOrNull("bits-per-sample")?.let { return it }
        return when (f.intOrNull(MediaFormat.KEY_PCM_ENCODING)) {
            AudioFormat.ENCODING_PCM_8BIT -> 8
            AudioFormat.ENCODING_PCM_16BIT -> 16
            AudioFormat.ENCODING_PCM_24BIT_PACKED -> 24
            AudioFormat.ENCODING_PCM_32BIT, AudioFormat.ENCODING_PCM_FLOAT -> 32
            else -> null
        }
    }

    private fun estimateBitrate(context: Context, uri: Uri, f: MediaFormat): Int? {
        val durationUs = if (f.containsKey(MediaFormat.KEY_DURATION)) f.getLong(MediaFormat.KEY_DURATION) else return null
        if (durationUs <= 0) return null
        val bytes = context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: return null
        if (bytes <= 0) return null
        return (bytes * 8_000_000L / durationUs).toInt()
    }

    private fun MediaFormat.intOrNull(key: String): Int? =
        if (containsKey(key)) runCatching { getInteger(key) }.getOrNull() else null
}
