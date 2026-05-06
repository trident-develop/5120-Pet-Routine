package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.Danger
import org.example.project.data.LocalAppStrings
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes

@Composable
fun AnimalEmojiArt(
    emoji: String,
    modifier: Modifier = Modifier,
    background: Brush = Brush.linearGradient(listOf(AppColors.Surface, AppColors.SurfaceElevated)),
    fontSize: Int = 56,
) {
    Box(
        modifier = modifier
            .background(background, AppShapes.medium),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, fontSize = fontSize.sp)
    }
}

@Composable
fun DangerBadge(danger: Danger, modifier: Modifier = Modifier) {
    val color = when (danger) {
        Danger.Low -> AppColors.Success
        Danger.Medium -> AppColors.Warning
        Danger.High -> AppColors.Danger
        Danger.Extreme -> Color(0xFFFF4757)
    }
    val s = LocalAppStrings.current
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.18f), AppShapes.pill)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = s.dangerLabel(danger),
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
        )
    }
}
