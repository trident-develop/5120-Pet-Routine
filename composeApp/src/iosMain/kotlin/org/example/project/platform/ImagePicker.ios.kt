@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package org.example.project.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import org.example.project.data.LocalAppStrings
import org.example.project.ui.components.AppDialog
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.writeToURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

private var currentImagePickerDelegate: NSObject? = null

@Composable
actual fun rememberImagePicker(): ImagePicker {
    var dialog by remember { mutableStateOf<IOSDialog?>(null) }
    var pendingCameraResult by remember { mutableStateOf<((String?) -> Unit)?>(null) }
    var pendingGalleryResult by remember { mutableStateOf<((String?) -> Unit)?>(null) }
    val s = LocalAppStrings.current

    when (dialog) {
        IOSDialog.SettingsCamera -> AppDialog(
            title = s.iosCameraDisabledTitle,
            body = s.iosCameraDisabledBody,
            emoji = "⚙️",
            confirmText = s.iosOpenSettings,
            onConfirm = {
                dialog = null
                openIOSAppSettings()
                pendingCameraResult?.invoke(null); pendingCameraResult = null
            },
            onDismiss = {
                dialog = null
                pendingCameraResult?.invoke(null); pendingCameraResult = null
            },
        )
        IOSDialog.SettingsGallery -> AppDialog(
            title = s.iosPhotosDisabledTitle,
            body = s.iosPhotosDisabledBody,
            emoji = "⚙️",
            confirmText = s.iosOpenSettings,
            onConfirm = {
                dialog = null
                openIOSAppSettings()
                pendingGalleryResult?.invoke(null); pendingGalleryResult = null
            },
            onDismiss = {
                dialog = null
                pendingGalleryResult?.invoke(null); pendingGalleryResult = null
            },
        )
        null -> Unit
    }

    return remember {
        object : ImagePicker {
            override fun captureFromCamera(onResult: (path: String?) -> Unit) {
                pendingCameraResult = onResult
                when (checkCameraStatus()) {
                    IOSPermissionStatus.Authorized -> presentCamera { uri ->
                        runOnMain {
                            pendingCameraResult?.invoke(uri); pendingCameraResult = null
                        }
                    }
                    IOSPermissionStatus.NotDetermined -> requestCameraAccess { status ->
                        if (status == IOSPermissionStatus.Authorized) {
                            presentCamera { uri ->
                                runOnMain {
                                    pendingCameraResult?.invoke(uri); pendingCameraResult = null
                                }
                            }
                        } else {
                            dialog = IOSDialog.SettingsCamera
                        }
                    }
                    IOSPermissionStatus.Denied -> dialog = IOSDialog.SettingsCamera
                }
            }

            override fun pickFromGallery(onResult: (path: String?) -> Unit) {
                pendingGalleryResult = onResult
                val deliver: (String?) -> Unit = { uri ->
                    runOnMain {
                        pendingGalleryResult?.invoke(uri); pendingGalleryResult = null
                    }
                }
                when (checkPhotoLibraryStatus()) {
                    IOSPhotoLibraryStatus.FullAccess -> presentGallery(deliver)
                    IOSPhotoLibraryStatus.LimitedAccess -> presentLimitedGrid(deliver)
                    IOSPhotoLibraryStatus.NotDetermined -> requestPhotoLibraryAccess { status ->
                        when (status) {
                            IOSPhotoLibraryStatus.FullAccess -> presentGallery(deliver)
                            IOSPhotoLibraryStatus.LimitedAccess -> presentLimitedGrid(deliver)
                            else -> dialog = IOSDialog.SettingsGallery
                        }
                    }
                    IOSPhotoLibraryStatus.Denied -> dialog = IOSDialog.SettingsGallery
                }
            }
        }
    }
}

private enum class IOSDialog { SettingsCamera, SettingsGallery }

private fun presentCamera(onResult: (String?) -> Unit) {
    runOnMain {
        val picker = UIImagePickerController()
        picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        val delegate = ImagePickerDelegate(onResult)
        currentImagePickerDelegate = delegate
        picker.delegate = delegate
        ViewControllerHolder.topController()?.presentViewController(picker, animated = true, completion = null)
    }
}

private fun presentGallery(onResult: (String?) -> Unit) {
    runOnMain {
        val picker = UIImagePickerController()
        picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        val delegate = ImagePickerDelegate(onResult)
        currentImagePickerDelegate = delegate
        picker.delegate = delegate
        ViewControllerHolder.topController()?.presentViewController(picker, animated = true, completion = null)
    }
}

/**
 * Custom picker for `.limited` photo-library access — system pickers ignore the
 * Limited Access selection because they run out-of-process. This grid lists only
 * PHAssets the user explicitly authorised.
 */
private fun presentLimitedGrid(onResult: (String?) -> Unit) {
    runOnMain {
        val holder = arrayOfNulls<UIViewController>(1)
        val vc = ComposeUIViewController {
            LimitedPhotoGrid(
                onPick = { path ->
                    holder[0]?.dismissViewControllerAnimated(true) { onResult(path) }
                },
                onCancel = {
                    holder[0]?.dismissViewControllerAnimated(true) { onResult(null) }
                },
                hostController = { holder[0] },
            )
        }
        holder[0] = vc
        ViewControllerHolder.topController()?.presentViewController(vc, animated = true, completion = null)
    }
}

private class ImagePickerDelegate(
    private val onResult: (String?) -> Unit,
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>,
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        picker.dismissViewControllerAnimated(true) {
            currentImagePickerDelegate = null
            onResult(image?.let(::saveJpeg))
        }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true) {
            currentImagePickerDelegate = null
            onResult(null)
        }
    }
}

private fun saveJpeg(image: UIImage): String? {
    val data: NSData = UIImageJPEGRepresentation(image, 0.85) ?: return null
    val tmpDir = NSTemporaryDirectory()
    val name = "pet_${NSUUID().UUIDString}.jpg"
    val path = "$tmpDir$name"
    val url = NSURL.fileURLWithPath(path)
    val ok = data.writeToURL(url, atomically = true)
    return if (ok) path else null
}
