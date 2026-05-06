package org.example.project.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object AppShapes {
    val small = RoundedCornerShape(10.dp)
    val medium = RoundedCornerShape(18.dp)
    val large = RoundedCornerShape(24.dp)
    val xLarge = RoundedCornerShape(32.dp)
    val pill = RoundedCornerShape(50)
}

private val DefaultFont = FontFamily.Default

private val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = DefaultFont, fontWeight = FontWeight.Black, fontSize = 36.sp, letterSpacing = (-0.5).sp),
    displayMedium = TextStyle(fontFamily = DefaultFont, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp),
    headlineLarge = TextStyle(fontFamily = DefaultFont, fontWeight = FontWeight.Bold, fontSize = 26.sp),
    headlineMedium = TextStyle(fontFamily = DefaultFont, fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleLarge = TextStyle(fontFamily = DefaultFont, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleMedium = TextStyle(fontFamily = DefaultFont, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = DefaultFont, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = DefaultFont, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 19.sp),
    labelLarge = TextStyle(fontFamily = DefaultFont, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, letterSpacing = 0.3.sp),
    labelMedium = TextStyle(fontFamily = DefaultFont, fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.2.sp),
    labelSmall = TextStyle(fontFamily = DefaultFont, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.4.sp),
)

private val AppMaterialShapes = Shapes(
    extraSmall = AppShapes.small,
    small = AppShapes.small,
    medium = AppShapes.medium,
    large = AppShapes.large,
    extraLarge = AppShapes.xLarge,
)

private fun materialColorScheme(palette: AppColorPalette, dark: Boolean) = if (dark) {
    darkColorScheme(
        primary = palette.accent,
        onPrimary = palette.backgroundDeep,
        secondary = palette.accentSoft,
        onSecondary = palette.backgroundDeep,
        background = palette.backgroundDeep,
        onBackground = palette.onSurface,
        surface = palette.surface,
        onSurface = palette.onSurface,
        surfaceVariant = palette.surfaceElevated,
        onSurfaceVariant = palette.onSurfaceMuted,
        error = palette.danger,
        onError = palette.backgroundDeep,
        outline = palette.cardBorder,
    )
} else {
    lightColorScheme(
        primary = palette.accent,
        onPrimary = Color.White,
        secondary = palette.accentSoft,
        onSecondary = palette.onSurface,
        background = palette.backgroundDeep,
        onBackground = palette.onSurface,
        surface = palette.surface,
        onSurface = palette.onSurface,
        surfaceVariant = palette.surfaceElevated,
        onSurfaceVariant = palette.onSurfaceMuted,
        error = palette.danger,
        onError = Color.White,
        outline = palette.cardBorder,
    )
}

@Composable
fun AppTheme(darkMode: Boolean = true, content: @Composable () -> Unit) {
    val palette = if (darkMode) DarkPalette else LightPalette
    CompositionLocalProvider(LocalAppColors provides palette) {
        MaterialTheme(
            colorScheme = materialColorScheme(palette, darkMode),
            typography = AppTypography,
            shapes = AppMaterialShapes,
            content = content,
        )
    }
}
