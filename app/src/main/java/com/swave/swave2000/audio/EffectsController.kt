package com.swave.swave2000.audio

import android.media.audiofx.BassBoost
import android.media.audiofx.Virtualizer

/**
 * Bass Boost and 3D (Virtualizer) attached to the player's audio session.
 * Both start disabled; while disabled the audio path is untouched.
 */
class EffectsController {
    var bassOn = false
        private set
    var virtualizerOn = false
        private set

    private var bass: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    /** (Re)creates the effects for a new audio session id. */
    fun attach(audioSessionId: Int) {
        release()
        if (audioSessionId == 0) return
        bass = runCatching { BassBoost(0, audioSessionId) }.getOrNull()
        virtualizer = runCatching { Virtualizer(0, audioSessionId) }.getOrNull()
        apply()
    }

    fun set(bass: Boolean, virtualizer: Boolean) {
        bassOn = bass
        virtualizerOn = virtualizer
        apply()
    }

    private fun apply() {
        bass?.let { fx ->
            runCatching {
                if (fx.strengthSupported) fx.setStrength(if (bassOn) STRENGTH else 0)
                fx.setEnabled(bassOn)
            }
        }
        virtualizer?.let { fx ->
            runCatching {
                if (fx.strengthSupported) fx.setStrength(if (virtualizerOn) STRENGTH else 0)
                fx.setEnabled(virtualizerOn)
            }
        }
    }

    fun release() {
        bass?.release(); bass = null
        virtualizer?.release(); virtualizer = null
    }

    private companion object {
        const val STRENGTH: Short = 800 // 0..1000
    }
}
