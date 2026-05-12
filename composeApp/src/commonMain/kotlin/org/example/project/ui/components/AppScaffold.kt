package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes

val LocalSnackbarHost = compositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState not provided")
}

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
    content: @Composable () -> Unit,
) {
    val systemInsets = WindowInsets.safeDrawing.asPaddingValues()
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val snackbarHostState = remember { SnackbarHostState() }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppColors.BackgroundDeep,
    ) {
        CompositionLocalProvider(LocalSnackbarHost provides snackbarHostState) {
            Box(modifier = Modifier.fillMaxSize().background(AppColors.backgroundGradient)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = systemInsets.calculateTopPadding())
                            .padding(contentPadding),
                    ) {
                        content()
                    }
                    if (!imeVisible) bottomBar()
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .padding(bottom = 96.dp + systemInsets.calculateBottomPadding()),
                ) { snackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        shape = AppShapes.medium,
                        containerColor = AppColors.SurfaceElevated,
                        contentColor = AppColors.OnSurface,
                        actionColor = AppColors.Accent,
                    )
                }
            }
        }
    }
}
