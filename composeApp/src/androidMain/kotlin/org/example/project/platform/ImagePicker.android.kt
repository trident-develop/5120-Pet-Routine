package org.example.project.platform

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.ui.components.AppDialog
import java.io.File

@Composable
actual fun rememberImagePicker(): ImagePicker {
    val context = LocalContext.current
    val activity = remember(context) { context as? Activity ?: error("ImagePicker requires Activity context") }
    val scope = rememberCoroutineScope()

    var pendingCameraResult by remember { mutableStateOf<((String?) -> Unit)?>(null) }
    var pendingGalleryResult by remember { mutableStateOf<((String?) -> Unit)?>(null) }
    var dialog by remember { mutableStateOf<PermissionDialog?>(null) }

    val cameraCaptureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val path = result.data?.getStringExtra(CameraCaptureActivity.EXTRA_PATH)
        val cb = pendingCameraResult
        pendingCameraResult = null
        cb?.invoke(path)
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        val cb = pendingGalleryResult
        pendingGalleryResult = null
        if (uri == null) {
            cb?.invoke(null)
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val path = withContext(Dispatchers.IO) { copyUriToInternal(activity, uri) }
            cb?.invoke(path)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCameraActivity(activity, cameraCaptureLauncher)
        } else {
            val status = checkPermission(activity, Manifest.permission.CAMERA)
            if (status == PermissionStatus.PermanentlyDenied) {
                dialog = PermissionDialog.SettingsCamera
            } else {
                pendingCameraResult?.invoke(null)
                pendingCameraResult = null
            }
        }
    }

    when (dialog) {
        PermissionDialog.RationaleCamera -> AppDialog(
            title = "Camera access",
            body = "We use the camera so you can take a photo of your pet. The image is stored only on this device.",
            emoji = "📷",
            confirmText = "Allow",
            onConfirm = {
                dialog = null
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onDismiss = {
                dialog = null
                pendingCameraResult?.invoke(null)
                pendingCameraResult = null
            },
        )
        PermissionDialog.SettingsCamera -> AppDialog(
            title = "Camera disabled",
            body = "Camera access has been turned off. Open Settings to enable it for this app.",
            emoji = "⚙️",
            confirmText = "Open Settings",
            onConfirm = {
                dialog = null
                openAppSettings(activity)
                pendingCameraResult?.invoke(null)
                pendingCameraResult = null
            },
            onDismiss = {
                dialog = null
                pendingCameraResult?.invoke(null)
                pendingCameraResult = null
            },
        )
        null -> Unit
    }

    return remember(activity) {
        object : ImagePicker {
            override fun captureFromCamera(onResult: (path: String?) -> Unit) {
                pendingCameraResult = onResult
                when (checkPermission(activity, Manifest.permission.CAMERA)) {
                    PermissionStatus.Granted -> launchCameraActivity(activity, cameraCaptureLauncher)
                    PermissionStatus.Denied -> dialog = PermissionDialog.RationaleCamera
                    PermissionStatus.PermanentlyDenied -> {
                        // First-time request will look like permanently-denied because rationale
                        // is false; only treat it as that after an explicit denial. Use a
                        // direct request first.
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            }

            override fun pickFromGallery(onResult: (path: String?) -> Unit) {
                pendingGalleryResult = onResult
                pickerLauncher.launch(
                    androidx.activity.result.PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    }
}

private enum class PermissionDialog { RationaleCamera, SettingsCamera }

private fun launchCameraActivity(
    activity: Activity,
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>,
) {
    launcher.launch(Intent(activity, CameraCaptureActivity::class.java))
}

private fun copyUriToInternal(context: Context, uri: Uri): String? {
    return try {
        val dir = File(context.filesDir, "pet_images").apply { mkdirs() }
        val target = File(dir, "pet_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        target.absolutePath
    } catch (_: Throwable) {
        null
    }
}
