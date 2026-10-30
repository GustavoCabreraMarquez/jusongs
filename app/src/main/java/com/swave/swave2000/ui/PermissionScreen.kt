package com.swave.swave2000.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.ButtonLabel
import com.swave.swave2000.components.DotMatrixText
import com.swave.swave2000.components.GemBadge
import com.swave.swave2000.components.LcdPanel
import com.swave.swave2000.components.TactileButton
import com.swave.swave2000.theme.*

@Composable
fun PermissionScreen(onGrant: () -> Unit, onOpenSettings: () -> Unit) {
    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GemBadge(size = 72.dp)
        LcdPanel(Modifier.fillMaxWidth()) {
            DotMatrixText("ACCESS REQUIRED")
            DotMatrixText("MUSIC + NOTIFICATIONS", style = LcdSubTextStyle)
        }
        Text(
            "S-WAVE 2000 needs to read your audio files and show playback controls. " +
                "Everything stays on this phone: no accounts, no ads, no tracking.",
            color = Y2KTextPrimary
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TactileButton(onClick = onGrant, modifier = Modifier.weight(1f)) { ButtonLabel("GRANT ACCESS") }
            TactileButton(onClick = onOpenSettings, modifier = Modifier.weight(1f)) { ButtonLabel("SETTINGS") }
        }
    }
}
