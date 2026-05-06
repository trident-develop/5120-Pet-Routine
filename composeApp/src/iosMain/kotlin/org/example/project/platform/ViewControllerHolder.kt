package org.example.project.platform

import platform.UIKit.UIViewController

object ViewControllerHolder {
    var rootController: UIViewController? = null

    fun topController(): UIViewController? {
        var current = rootController
        while (current?.presentedViewController != null) {
            current = current.presentedViewController
        }
        return current
    }
}
