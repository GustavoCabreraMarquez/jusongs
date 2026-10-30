package com.swave.swave2000.service

import android.os.Bundle
import androidx.media3.session.SessionCommand

/** Custom session command carrying the Bass / 3D flags from the UI to the service. */
object EffectsCommand {
    const val ACTION = "com.swave.swave2000.SET_EFFECTS"
    const val KEY_BASS = "bass"
    const val KEY_VIRTUALIZER = "virtualizer"

    val command = SessionCommand(ACTION, Bundle.EMPTY)

    fun bundle(bass: Boolean, virtualizer: Boolean) = Bundle().apply {
        putBoolean(KEY_BASS, bass)
        putBoolean(KEY_VIRTUALIZER, virtualizer)
    }
}
