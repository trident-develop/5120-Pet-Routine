package org.example.project

import androidx.compose.ui.window.ComposeUIViewController
import org.example.project.platform.ViewControllerHolder
import platform.UIKit.UIColor

fun MainViewController() = ComposeUIViewController { App() }
    .apply {
        view.backgroundColor = UIColor(
            red = 0x14 / 255.0,
            green = 0x1A / 255.0,
            blue = 0x24 / 255.0,
            alpha = 1.0,
        )
    }
    .also { ViewControllerHolder.rootController = it }
