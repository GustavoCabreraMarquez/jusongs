package com.swave.swave2000.ui

import com.swave.swave2000.data.TrackFormat

fun formatDuration(ms: Long): String {
    val totalSeconds = (ms.coerceAtLeast(0) / 1000).toInt()
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}

/** Compact label for the progress bar: `FLAC 24/96` for lossless, `MP3 320` for lossy. */
fun TrackFormat.shortLabel(): String = buildString {
    append(codec)
    if (lossless) {
        if (bitDepth != null || sampleRateHz != null) {
            append(' ').append(bitDepth ?: "")
            sampleRateHz?.let { append('/').append(it / 1000) }
        }
    } else {
        bitrateKbps?.let { append(' ').append(it) }
    }
}
