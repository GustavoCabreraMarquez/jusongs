package com.swave.swave2000.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.AppIcons
import com.swave.swave2000.components.glassBlur
import com.swave.swave2000.theme.LocalThemeTokens

enum class NavTab(val label: String, val icon: ImageVector) {
    Home("Home", AppIcons.Home),
    Albums("Albums", AppIcons.Albums),
    Songs("Songs", AppIcons.Songs)
}

/** Glass bottom bar: line icon over label; the active item uses `ink` at weight 600, the others `ink2`. */
@Composable
fun BottomNav(selected: NavTab, onSelect: (NavTab) -> Unit, modifier: Modifier = Modifier) {
    val t = LocalThemeTokens.current
    Column(modifier.fillMaxWidth().glassBlur(RectangleShape, border = false).navigationBarsPadding()) {
        HorizontalDivider(color = t.line)
        Row(Modifier.fillMaxWidth().height(68.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            NavTab.entries.forEach { tab ->
                val active = tab == selected
                val color = if (active) t.ink else t.ink2
                Column(
                    Modifier.weight(1f).height(68.dp).clickable { onSelect(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                ) {
                    Icon(tab.icon, null, Modifier.size(22.dp), tint = color)
                    Text(
                        tab.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                        color = color
                    )
                }
            }
        }
    }
}
