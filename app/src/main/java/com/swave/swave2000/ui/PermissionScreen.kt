package com.swave.swave2000.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.AppIcons
import com.swave.swave2000.components.ButtonLabel
import com.swave.swave2000.components.TactileButton
import com.swave.swave2000.components.glass
import com.swave.swave2000.theme.LocalThemeTokens

@Composable
fun PermissionScreen(onGrant: () -> Unit, onOpenSettings: () -> Unit) {
    val t = LocalThemeTokens.current
    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(AppIcons.Songs, null, Modifier.clip(CircleShape).background(t.acc).padding(18.dp).size(36.dp), tint = t.onacc)
        Column(Modifier.fillMaxWidth().glass().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Access required", style = MaterialTheme.typography.titleLarge, color = t.ink)
            Text("Music + notifications", style = MaterialTheme.typography.bodySmall, color = t.ink2)
            Text(
                "JuSongs needs to read your audio files and show playback controls. " +
                    "Everything stays on this phone: no accounts, no ads, no tracking.",
                style = MaterialTheme.typography.bodyLarge,
                color = t.ink,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TactileButton(onClick = onGrant, solid = true, modifier = Modifier.weight(1f)) { ButtonLabel("Grant access") }
            TactileButton(onClick = onOpenSettings, modifier = Modifier.weight(1f)) { ButtonLabel("Settings") }
        }
    }
}
