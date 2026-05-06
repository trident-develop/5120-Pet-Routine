package org.example.project.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes

enum class ButtonStyle { Primary, Secondary, Ghost }

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Primary,
    leadingEmoji: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = spring(stiffness = 420f, dampingRatio = 0.5f),
        label = "btn-scale",
    )

    val (bg, fg, borderColor) = when (style) {
        ButtonStyle.Primary -> Triple(AppColors.Accent, AppColors.BackgroundDeep, Color.Transparent)
        ButtonStyle.Secondary -> Triple(AppColors.SurfaceElevated, AppColors.OnSurface, AppColors.CardBorder)
        ButtonStyle.Ghost -> Triple(Color.Transparent, AppColors.OnSurface, AppColors.CardBorder)
    }

    Row(
        modifier = modifier
            .scale(scale)
            .height(50.dp)
            .clip(AppShapes.medium)
            .background(if (enabled) bg else AppColors.SurfaceMuted, AppShapes.medium)
            .border(BorderStroke(1.dp, borderColor), AppShapes.medium)
            .clickable(
                interactionSource = interaction,
                indication = androidx.compose.foundation.LocalIndication.current,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 22.dp)
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (leadingEmoji != null) {
            Text(text = leadingEmoji, color = fg)
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            color = if (enabled) fg else AppColors.OnSurfaceDim,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
