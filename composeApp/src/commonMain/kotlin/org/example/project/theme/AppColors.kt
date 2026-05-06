package org.example.project.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class AppColorPalette(
    val backgroundDeep: Color,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceMuted: Color,
    val cardBorder: Color,
    val divider: Color,
    val accent: Color,
    val accentSoft: Color,
    val accentDim: Color,
    val onSurface: Color,
    val onSurfaceMuted: Color,
    val onSurfaceDim: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
)

val DarkPalette = AppColorPalette(
    backgroundDeep = Color(0xFF0E131B),
    background = Color(0xFF141A24),
    surface = Color(0xFF1E2530),
    surfaceElevated = Color(0xFF28313F),
    surfaceMuted = Color(0xFF171D27),
    cardBorder = Color(0x1FB8C5DA),
    divider = Color(0x16B8C5DA),
    accent = Color(0xFFF6C445),
    accentSoft = Color(0xFFFFD166),
    accentDim = Color(0x33F6C445),
    onSurface = Color(0xFFF1F3F8),
    onSurfaceMuted = Color(0xFFAFB8C7),
    onSurfaceDim = Color(0xFF78818F),
    success = Color(0xFF6EE7A6),
    warning = Color(0xFFFFB454),
    danger = Color(0xFFFF6B6B),
)

val LightPalette = AppColorPalette(
    backgroundDeep = Color(0xFFE9ECF2),
    background = Color(0xFFF6F7FA),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    surfaceMuted = Color(0xFFEEF1F5),
    cardBorder = Color(0x1A1B2538),
    divider = Color(0x141B2538),
    accent = Color(0xFFD4A20F),
    accentSoft = Color(0xFFE2B73A),
    accentDim = Color(0x33F6C445),
    onSurface = Color(0xFF111726),
    onSurfaceMuted = Color(0xFF515A6E),
    onSurfaceDim = Color(0xFF838C9E),
    success = Color(0xFF1F9E6A),
    warning = Color(0xFFC97A00),
    danger = Color(0xFFD64545),
)

val LocalAppColors = compositionLocalOf { DarkPalette }

object AppColors {
    val BackgroundDeep: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.backgroundDeep
    val Background: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.background
    val Surface: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.surface
    val SurfaceElevated: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.surfaceElevated
    val SurfaceMuted: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.surfaceMuted
    val CardBorder: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.cardBorder
    val Divider: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.divider

    val Accent: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.accent
    val AccentSoft: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.accentSoft
    val AccentDim: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.accentDim

    val OnSurface: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.onSurface
    val OnSurfaceMuted: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.onSurfaceMuted
    val OnSurfaceDim: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.onSurfaceDim

    val Success: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.success
    val Warning: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.warning
    val Danger: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.danger

    val accentGradient: Brush
        @Composable @ReadOnlyComposable
        get() = Brush.linearGradient(listOf(Accent, AccentSoft))

    val surfaceGradient: Brush
        @Composable @ReadOnlyComposable
        get() = Brush.verticalGradient(listOf(Surface, SurfaceMuted))

    val backgroundGradient: Brush
        @Composable @ReadOnlyComposable
        get() = Brush.verticalGradient(listOf(BackgroundDeep, Background))
}
