package org.example.project.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AnimatedCounter(
    value: Int,
    suffix: String = "",
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight = FontWeight.SemiBold,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AnimatedContent(
            targetState = value,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInVertically { -it } + fadeIn(tween(220))).togetherWith(
                        slideOutVertically { it } + fadeOut(tween(180))
                    )
                } else {
                    (slideInVertically { it } + fadeIn(tween(220))).togetherWith(
                        slideOutVertically { -it } + fadeOut(tween(180))
                    )
                }
            },
            label = "counter",
        ) { v ->
            Text(text = v.toString(), color = color, style = style, fontWeight = fontWeight)
        }
        if (suffix.isNotEmpty()) {
            Text(text = suffix, color = color, style = style, fontWeight = fontWeight)
        }
    }
}
