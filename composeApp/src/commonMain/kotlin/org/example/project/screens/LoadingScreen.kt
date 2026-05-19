package org.example.project.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.DisableBack
import org.example.project.data.LocalAppStrings
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes
import org.jetbrains.compose.resources.painterResource
import petroutine.composeapp.generated.resources.Res
import petroutine.composeapp.generated.resources.bg_1
import petroutine.composeapp.generated.resources.chicken
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LoadingScreen() {
    val s = LocalAppStrings.current
    DisableBack()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.backgroundGradient),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedConstellation(modifier = Modifier.fillMaxSize())

        Image(
            painter = painterResource(Res.drawable.bg_1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BreathingGlow()
            Spacer(Modifier.height(98.dp))
            Text(
                text = s.loadingTitle,
                color = AppColors.OnSurface,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp,
            )
            Spacer(Modifier.height(28.dp))
            InfiniteProgressBar()
            Spacer(Modifier.height(20.dp))
            PawPrintTrail()
        }
    }
}

@Composable
private fun BreathingGlow() {
    val transition = rememberInfiniteTransition(label = "glow")
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.75f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "pulse",
    )
    val alpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "alpha",
    )
    val rotate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "rot",
    )
    val accent = AppColors.Accent
    val accentSoft = AppColors.AccentSoft
    val accentDim = AppColors.AccentDim
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .scale(pulse)
                .alpha(alpha),
        ) {
            val r = size.minDimension / 2f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(accentDim, Color.Transparent),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = r,
                ),
            )
            drawCircle(
                color = accent.copy(alpha = 0.45f),
                radius = r * 0.55f,
                center = Offset(size.width / 2f, size.height / 2f),
            )
        }
        Canvas(
            modifier = Modifier
                .size(250.dp)
                .scale(pulse * 0.95f),
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = size.minDimension / 2f
            for (i in 0 until 8) {
                val a = (i * 45f + rotate) * (kotlin.math.PI / 180f).toFloat()
                val px = cx + cos(a) * r * 0.95f
                val py = cy + sin(a) * r * 0.95f
                drawCircle(
                    color = accentSoft.copy(alpha = 0.85f),
                    radius = 3f,
                    center = Offset(px, py),
                )
            }
        }
        Image(
            painter = painterResource(Res.drawable.chicken),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun InfiniteProgressBar() {
    val transition = rememberInfiniteTransition(label = "progress")
    val pos by transition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing)),
        label = "pos",
    )
    val accent = AppColors.Accent
    val accentSoft = AppColors.AccentSoft
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(AppColors.SurfaceMuted, AppShapes.pill),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width * 0.4f
            val x = pos * size.width
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, accent, accentSoft, Color.Transparent),
                ),
                topLeft = Offset(x - w / 2f, 0f),
                size = androidx.compose.ui.geometry.Size(w, size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f),
            )
        }
    }
}

@Composable
private fun PawPrintTrail() {
    val transition = rememberInfiniteTransition(label = "paws")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "phase",
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(4) { idx ->
            val pawAlpha = ((1f - ((phase - idx).mod(4f) / 4f)).coerceIn(0f, 1f)) * 0.9f + 0.1f
            Text(
                text = "🐾",
                fontSize = 22.sp,
                modifier = Modifier.alpha(pawAlpha),
            )
        }
    }
}

@Composable
private fun AnimatedConstellation(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "stars")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "drift",
    )
    val accent = AppColors.Accent
    Canvas(modifier = modifier) {
        val seeds = listOf(
            0.12f to 0.18f,
            0.38f to 0.08f,
            0.61f to 0.22f,
            0.85f to 0.12f,
            0.22f to 0.78f,
            0.55f to 0.84f,
            0.81f to 0.71f,
            0.06f to 0.46f,
            0.92f to 0.42f,
            0.48f to 0.5f,
        )
        seeds.forEachIndexed { i, (sx, sy) ->
            val a = (drift + i * 0.13f).mod(1f)
            val twinkle = 0.4f + 0.6f * (0.5f + 0.5f * sin((a * 6.28f).toDouble()).toFloat())
            drawCircle(
                color = accent.copy(alpha = 0.18f * twinkle),
                radius = (1.5f + i % 3) * 1.2f,
                center = Offset(sx * size.width, sy * size.height),
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)

@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 360,
    heightDp = 640
)

@Preview(
    name = "mdpi (160)",
    widthDp = 320,
    heightDp = 680,
    fontScale = 1.0f,
    showBackground = true,
    showSystemUi = true
)

@Preview(
    name = "hdpi (240)",
    widthDp = 450,
    heightDp = 800,
    fontScale = 1.0f,
    showBackground = true,
    showSystemUi = true
)

@Composable
private fun ScreenPreview() {

    LoadingScreen()
}