package org.example.project

import androidx.compose.runtime.Composable

@Composable
actual fun Gray(
    loading: @Composable (() -> Unit),
    noInternet: @Composable ((onRetry: () -> Unit) -> Unit),
    white: @Composable (() -> Unit),
) {
    GrayShellImpl(loading = loading, noInternet = noInternet, white = white)
}
