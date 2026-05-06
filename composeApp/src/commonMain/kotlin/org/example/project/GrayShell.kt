package org.example.project

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

private const val LOADING_DURATION_MS = 2000L

@Composable
internal fun GrayShellImpl(
    loading: @Composable () -> Unit,
    @Suppress("UNUSED_PARAMETER") noInternet: @Composable (onRetry: () -> Unit) -> Unit,
    white: @Composable () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(LOADING_DURATION_MS)
            isLoading = false
        }
    }

    AnimatedContent(
        targetState = isLoading,
        transitionSpec = {
            fadeIn(tween(420)) togetherWith fadeOut(tween(280))
        },
        label = "loading-to-white",
    ) { showLoading ->
        if (showLoading) loading() else white()
    }
}
