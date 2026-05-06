package org.example.project.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UIViewContentMode

@Composable
actual fun PlatformImage(path: String, modifier: Modifier) {
    UIKitView(
        factory = {
            UIImageView().apply {
                contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
                clipsToBounds = true
                image = UIImage.imageWithContentsOfFile(path)
            }
        },
        update = { view ->
            view.image = UIImage.imageWithContentsOfFile(path)
        },
        modifier = modifier,
    )
}
