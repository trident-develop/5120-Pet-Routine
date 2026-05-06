package org.example.project.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: RoundedCornerShape = AppShapes.medium,
    background: Color = AppColors.Surface,
    backgroundBrush: Brush? = null,
    border: BorderStroke? = BorderStroke(1.dp, AppColors.CardBorder),
    contentPadding: Dp = 16.dp,
    pressScale: Float = 0.98f,
    content: @Composable () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && onClick != null) pressScale else 1f,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.6f),
        label = "card-scale",
    )
    val base = Modifier
        .scale(scale)
        .clip(shape)
        .let { if (backgroundBrush != null) it.background(backgroundBrush, shape) else it.background(background, shape) }
        .let { if (border != null) it.border(border, shape) else it }
        .let {
            if (onClick != null) {
                it.clickable(
                    interactionSource = interaction,
                    indication = LocalIndication.current,
                    onClick = onClick,
                )
            } else it
        }
    Box(
        modifier = base
            .then(modifier)
            .padding(contentPadding),
    ) {
        content()
    }
}
