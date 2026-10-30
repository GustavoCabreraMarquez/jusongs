package com.swave.swave2000.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.withFrameNanos
import com.swave.swave2000.audio.SpectrumMath
import com.swave.swave2000.components.ArtworkImage
import com.swave.swave2000.components.BevelType
import com.swave.swave2000.components.BeveledContainer
import com.swave.swave2000.components.ButtonLabel
import com.swave.swave2000.components.DotMatrixText
import com.swave.swave2000.components.FormatBadge
import com.swave.swave2000.components.GemBadge
import com.swave.swave2000.components.LcdPanel
import com.swave.swave2000.components.RotaryKnob
import com.swave.swave2000.components.TactileButton
import com.swave.swave2000.theme.*
import kotlin.math.max

@Composable
fun NowPlayingScreen(vm: PlayerViewModel, onClose: () -> Unit) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val volume by vm.volume.collectAsStateWithLifecycle()
    val spectrum = vm.spectrum.collectAsStateWithLifecycle()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TactileButton(onClick = onClose) { ButtonLabel("◀ LIBRARY") }
            Spacer(Modifier.weight(1f))
            GemBadge(size = 28.dp)
        }

        LcdPanel(Modifier.fillMaxWidth()) {
            DotMatrixText(ui.title, Modifier.fillMaxWidth(), marquee = true)
            DotMatrixText(ui.artist, Modifier.fillMaxWidth(), style = LcdSubTextStyle, marquee = true)
            Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                DotMatrixText("${formatDuration(ui.positionMs)} / ${formatDuration(ui.durationMs)}", style = LcdSubTextStyle)
                Spacer(Modifier.weight(1f))
                DotMatrixText(ui.bandMode.label, style = LcdSubTextStyle)
            }
            SpectrumBars(spectrum, active = ui.isPlaying, Modifier.fillMaxWidth().height(96.dp).padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                ui.format?.let { FormatBadge(it.label(), lossless = it.lossless) }
                if (ui.format?.lossless == true) DotMatrixText("LOSSLESS", style = LcdSubTextStyle)
            }
        }

        BeveledContainer(
            Modifier.size(160.dp).align(Alignment.CenterHorizontally),
            type = BevelType.Recessed,
            backgroundColor = Y2KSageGreenDark
        ) {
            ArtworkImage(ui.artworkUri, Modifier.fillMaxSize(), ui.title)
        }

        BeveledContainer(Modifier.fillMaxWidth(), cornerRadius = 12.dp, backgroundColor = Y2KWarmCream) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    TactileButton(onClick = vm::previous, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.SkipPrevious, "Previous", tint = Y2KTextPrimary)
                    }
                    TactileButton(onClick = vm::togglePlay, modifier = Modifier.weight(1.4f)) {
                        Icon(
                            if (ui.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            if (ui.isPlaying) "Pause" else "Play",
                            tint = Y2KTextPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    TactileButton(onClick = vm::next, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.SkipNext, "Next", tint = Y2KTextPrimary)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TactileButton(
                        onClick = { vm.eject(); onClose() },
                        modifier = Modifier.weight(1f),
                        backgroundColor = Y2KAccentAmber
                    ) { ButtonLabel("EJECT") }
                    TactileButton(onClick = vm::cycleBand, modifier = Modifier.weight(1f)) { ButtonLabel("BAND") }
                    TactileButton(onClick = vm::toggleBass, latched = ui.bassOn, modifier = Modifier.weight(1f)) { ButtonLabel("BASS") }
                    TactileButton(onClick = vm::toggleVirtualizer, latched = ui.virtualizerOn, modifier = Modifier.weight(1f)) { ButtonLabel("3D") }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ButtonLabel("MIN")
                    RotaryKnob(volume, vm::setVolume, Modifier.padding(horizontal = 16.dp), diameter = 104.dp)
                    ButtonLabel("MAX")
                }
            }
        }
    }
}

private const val SEGMENTS = 12

/** LED-style bar graph; bars rise instantly and fall gradually. */
@Composable
private fun SpectrumBars(levels: State<FloatArray>, active: Boolean, modifier: Modifier = Modifier) {
    val shown = remember { FloatArray(SpectrumMath.BANDS) }
    var frame by remember { mutableIntStateOf(0) }

    LaunchedEffect(active) {
        while (true) {
            withFrameNanos {
                val target = levels.value
                for (i in shown.indices) {
                    val t = if (active) target.getOrElse(i) { 0f } else 0f
                    shown[i] = if (t > shown[i]) t else max(t, shown[i] - 0.04f)
                }
                frame++
            }
            if (!active && shown.all { it == 0f }) break
        }
    }

    Canvas(modifier) {
        frame // redraw on every animation frame
        val gap = 3.dp.toPx()
        val barW = (size.width - gap * (shown.size - 1)) / shown.size
        val segH = (size.height - gap * (SEGMENTS - 1)) / SEGMENTS
        for (i in shown.indices) {
            val lit = (shown[i] * SEGMENTS).toInt()
            for (s in 0 until SEGMENTS) {
                drawRect(
                    color = if (s < lit) Y2KLcdText else Y2KLcdGrid,
                    topLeft = Offset(i * (barW + gap), size.height - (s + 1) * segH - s * gap),
                    size = Size(barW, segH)
                )
            }
        }
    }
}
