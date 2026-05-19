package org.example.project

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable

@SuppressLint("ContextCastToActivity")
@Composable
actual fun Gray(
    loading: @Composable (() -> Unit),
    noInternet: @Composable ((onRetry: () -> Unit) -> Unit),
    white: @Composable (() -> Unit),
) {
//    GrayShellImpl(loading = loading, noInternet = noInternet, white = white)
}
