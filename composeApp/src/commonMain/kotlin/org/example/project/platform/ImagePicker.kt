package org.example.project.platform

import androidx.compose.runtime.Composable

interface ImagePicker {
    fun pickFromGallery(onResult: (path: String?) -> Unit)
    fun captureFromCamera(onResult: (path: String?) -> Unit)
}

@Composable
expect fun rememberImagePicker(): ImagePicker
