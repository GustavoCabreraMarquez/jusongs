package com.swave.swave2000.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.*

/** Small silver pill with an amber LED when the track is lossless. */
@Composable
fun FormatBadge(label: String, modifier: Modifier = Modifier, lossless: Boolean = false) {
    Row(
        modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Y2KMetallicBadge)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (lossless) {
            Spacer(Modifier.size(6.dp).clip(CircleShape).background(Y2KAccentAmber))
            Spacer(Modifier.width(4.dp))
        }
        Text(label, style = LcdSubTextStyle.copy(color = Y2KTextPrimary))
    }
}
