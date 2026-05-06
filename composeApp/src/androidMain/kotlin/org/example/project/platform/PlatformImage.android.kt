package org.example.project.platform

import android.net.Uri
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File

@Composable
actual fun PlatformImage(path: String, modifier: Modifier) {
    val uri = remember(path) {
        when {
            path.startsWith("content://") || path.startsWith("file://") -> Uri.parse(path)
            else -> Uri.fromFile(File(path))
        }
    }
    AndroidView(
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageURI(uri)
            }
        },
        update = { view ->
            view.setImageURI(null)
            view.setImageURI(uri)
        },
        modifier = modifier,
    )
}
