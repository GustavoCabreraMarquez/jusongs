package com.swave.swave2000.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swave.swave2000.audio.SpectrumMath
import com.swave.swave2000.components.AppIcons
import com.swave.swave2000.components.ArtworkImage
import com.swave.swave2000.components.ButtonLabel
import com.swave.swave2000.components.CircleButton
import com.swave.swave2000.components.ProgressPanel
import com.swave.swave2000.components.TactileButton
import com.swave.swave2000.components.ThinSlider
import com.swave.swave2000.components.glass
import com.swave.swave2000.theme.LocalThemeTokens
import com.swave.swave2000.theme.MonoSmallStyle
import com.swave.swave2000.theme.NightTokens
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NowPlayingScreen(vm: PlayerViewModel, onClose: () -> Unit, modifier: Modifier = Modifier) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val volume by vm.volume.collectAsStateWithLifecycle()
    val spectrum = vm.spectrum.collectAsStateWithLifecycle()
    val t = LocalThemeTokens.current

    var artFullscreen by remember { mutableStateOf(false) }

    Box(modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth()) { CircleButton(AppIcons.Back, "Back to library", onClose) }

            val coverShape = RoundedCornerShape(24.dp)
            ArtworkImage(
                ui.artworkUri,
                Modifier.size(260.dp).shadow(20.dp, coverShape).clip(coverShape).clickable { artFullscreen = true },
                ui.title
            )

            Column(
                Modifier.fillMaxWidth().glass().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        ui.title, style = MaterialTheme.typography.titleLarge, color = t.ink, maxLines = 1,
                        textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().basicMarquee()
                    )
                    Text(
                        ui.artist, style = MaterialTheme.typography.bodySmall, color = t.ink2, maxLines = 1,
                        textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().basicMarquee()
                    )
                }

                ProgressPanel(
                    progress = if (ui.durationMs > 0) ui.positionMs.toFloat() / ui.durationMs else 0f,
                    elapsed = formatDuration(ui.positionMs),
                    remaining = "-" + formatDuration(ui.durationMs - ui.positionMs),
                    format = ui.format?.shortLabel()
                )

                SpectrumBars(spectrum, active = ui.isPlaying, Modifier.fillMaxWidth().height(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(28.dp), verticalAlignment = Alignment.CenterVertically) {
                    CircleButton(AppIcons.Previous, "Previous", vm::previous)
                    CircleButton(
                        if (ui.isPlaying) AppIcons.Pause else AppIcons.Play,
                        if (ui.isPlaying) "Pause" else "Play",
                        vm::togglePlay, solid = true, size = 68.dp
                    )
                    CircleButton(AppIcons.Next, "Next", vm::next)
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("VOL", style = MonoSmallStyle, color = t.ink2)
                    Spacer(Modifier.width(12.dp))
                    ThinSlider(volume, vm::setVolume, Modifier.weight(1f))
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
                    TactileButton(onClick = vm::cycleBand, latched = ui.bandMode != BandMode.Off) { ButtonLabel(ui.bandMode.label.lowercase().replaceFirstChar { it.uppercase() }) }
                    TactileButton(onClick = vm::toggleBass, latched = ui.bassOn) { ButtonLabel("Bass") }
                    TactileButton(onClick = vm::toggleVirtualizer, latched = ui.virtualizerOn) { ButtonLabel("3D") }
                }
                TactileButton(onClick = { vm.eject(); onClose() }) { ButtonLabel("Eject") }
            }
        }

        if (artFullscreen) {
            BackHandler { artFullscreen = false }
            // Black backdrop in every theme, so the back button uses the light night tokens.
            CompositionLocalProvider(LocalThemeTokens provides NightTokens) {
                Box(Modifier.fillMaxSize().background(Color.Black)) {
                    ArtworkImage(ui.artworkUri, Modifier.fillMaxSize(), ui.title, ContentScale.Fit)
                    CircleButton(AppIcons.Back, "Close", { artFullscreen = false }, Modifier.align(Alignment.TopStart).padding(12.dp))
                }
            }
        }
    }
}

/** Slim spectrum bars in `acc`; bars rise instantly and fall gradually. */
@Composable
private fun SpectrumBars(levels: State<FloatArray>, active: Boolean, modifier: Modifier = Modifier) {
    val color = LocalThemeTokens.current.acc
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
        val gap = 4.dp.toPx()
        val barW = (size.width - gap * (shown.size - 1)) / shown.size
        val minH = 2.dp.toPx()
        for (i in shown.indices) {
            val h = max(minH, shown[i] * size.height)
            drawRoundRect(
                color = color.copy(alpha = 0.85f),
                topLeft = Offset(i * (barW + gap), size.height - h),
                size = Size(barW, h),
                cornerRadius = CornerRadius(barW / 2)
            )
        }
    }
}
