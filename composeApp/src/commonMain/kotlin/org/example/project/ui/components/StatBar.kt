package org.example.project.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes

@Composable
fun StatBar(
    label: String,
    value: Int,
    max: Int = 100,
    color: Color = AppColors.Accent,
    modifier: Modifier = Modifier,
) {
    val progress by animateFloatAsState(
        targetValue = (value.toFloat() / max).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 700),
        label = "stat",
    )
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = label, color = AppColors.OnSurfaceMuted, fontWeight = FontWeight.Medium)
            Spacer(Modifier.weight(1f))
            AnimatedCounter(value = value, suffix = "%", color = AppColors.OnSurface)
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(AppColors.SurfaceMuted, AppShapes.pill),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .background(
                        Brush.horizontalGradient(listOf(color, AppColors.AccentSoft)),
                        AppShapes.pill,
                    ),
            )
        }
    }
}
