@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package org.example.project.platform

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.project.data.LocalAppStrings
import org.example.project.ui.components.LocalSnackbarHost
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
import platform.darwin.NSObject

private var currentImagePickerDelegate: NSObject? = null

@Composable
actual fun rememberImagePicker(): ImagePicker {
    var pendingCameraResult by remember { mutableStateOf<((String?) -> Unit)?>(null) }
    var pendingGalleryResult by remember { mutableStateOf<((String?) -> Unit)?>(null) }
    val snackbar = LocalSnackbarHost.current
    val scope = rememberCoroutineScope()
    val s = LocalAppStrings.current
    val cameraDeniedToast = s.iosCameraPermissionToast

    return remember {
        object : ImagePicker {
            override fun captureFromCamera(onResult: (path: String?) -> Unit) {
                pendingCameraResult = onResult
                val deliverCamera: (String?) -> Unit = { uri ->
                    runOnMain {
                        pendingCameraResult?.invoke(uri); pendingCameraResult = null
                    }
                }
                when (checkCameraStatus()) {
                    IOSPermissionStatus.Authorized -> presentCamera(deliverCamera)
                    IOSPermissionStatus.NotDetermined -> requestCameraAccess { status ->
                        if (status == IOSPermissionStatus.Authorized) {
                            presentCamera(deliverCamera)
                        } else {
                            deliverCamera(null)
                            showToast(scope, snackbar, cameraDeniedToast)
                        }
                    }
                    IOSPermissionStatus.Denied -> {
                        deliverCamera(null)
                        showToast(scope, snackbar, cameraDeniedToast)
                    }
                }
            }

            override fun pickFromGallery(onResult: (path: String?) -> Unit) {
                pendingGalleryResult = onResult
                presentGallery { uri ->
                    runOnMain {
                        pendingGalleryResult?.invoke(uri); pendingGalleryResult = null
                    }
                }
            }
        }
    }
}

private fun showToast(scope: CoroutineScope, host: SnackbarHostState, message: String) {
    scope.launch { host.showSnackbar(message) }
}

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
