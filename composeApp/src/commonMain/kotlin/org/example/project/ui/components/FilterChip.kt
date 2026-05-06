package org.example.project.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes

@Composable
fun AppFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    large: Boolean = false,
) {
    val bg by animateColorAsState(
        if (selected) AppColors.Accent else AppColors.SurfaceElevated,
        label = "chip-bg",
    )
    val fg by animateColorAsState(
        if (selected) AppColors.BackgroundDeep else AppColors.OnSurfaceMuted,
        label = "chip-fg",
    )
    val border by animateColorAsState(
        if (selected) AppColors.Accent else AppColors.CardBorder,
        label = "chip-border",
    )
    val scale by animateFloatAsState(
        if (selected) 1.04f else 1f,
        animationSpec = spring(stiffness = 360f, dampingRatio = 0.5f),
        label = "chip-scale",
    )
    Text(
        text = text,
        color = fg,
        fontWeight = if (large) FontWeight.SemiBold else FontWeight.Medium,
        fontSize = if (large) 16.sp else 14.sp,
        modifier = modifier
            .scale(scale)
            .clip(AppShapes.pill)
            .background(bg, AppShapes.pill)
            .border(1.dp, border, AppShapes.pill)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick,
            )
            .padding(
                horizontal = if (large) 20.dp else 14.dp,
                vertical = if (large) 12.dp else 8.dp,
            ),
    )
}
