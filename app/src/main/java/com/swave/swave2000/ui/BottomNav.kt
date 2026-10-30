package com.swave.swave2000.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swave.swave2000.theme.*

enum class NavTab(val label: String, val glyph: String) {
    Home("Home", "⌂"),
    Albums("Albums", "◉"),
    Songs("Songs", "♪")
}

/** Spotify-style bottom bar: icon over label, active item highlighted. */
@Composable
fun BottomNav(selected: NavTab, onSelect: (NavTab) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().background(Y2KSageGreenDark).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavTab.entries.forEach { tab ->
            val active = tab == selected
            Column(
                Modifier.weight(1f).clickable { onSelect(tab) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(tab.glyph, fontSize = 22.sp, color = if (active) Y2KTextPrimary else Y2KTextSecondary)
                Text(
                    tab.label,
                    fontSize = 11.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                    color = if (active) Y2KTextPrimary else Y2KTextSecondary
                )
            }
        }
    }
}
