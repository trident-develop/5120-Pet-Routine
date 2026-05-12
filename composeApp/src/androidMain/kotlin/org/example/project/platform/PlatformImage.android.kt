package org.example.project.platform

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File

@Composable
actual fun PlatformImage(path: String, modifier: Modifier) {
    val context = LocalContext.current
    val bitmap = remember(path) {
        runCatching {
            val isContent = path.startsWith("content://")
            val isFileUri = path.startsWith("file://")
            when {
                isContent -> context.contentResolver.openInputStream(Uri.parse(path))
                isFileUri -> File(Uri.parse(path).path ?: return@runCatching null).inputStream()
                else -> File(path).takeIf { it.exists() }?.inputStream()
            }?.use(BitmapFactory::decodeStream)
        }.getOrNull()
    } ?: return

    AndroidView(
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageBitmap(bitmap)
            }
        },
        update = { view -> view.setImageBitmap(bitmap) },
        modifier = modifier,
    )
}
