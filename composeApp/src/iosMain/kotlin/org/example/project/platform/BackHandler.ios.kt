package org.example.project.platform

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS has no system back button; navigation is handled with UIKit gestures.
}
