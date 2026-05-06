package org.example.project.platform

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

enum class PermissionStatus { Granted, Denied, PermanentlyDenied }

internal fun checkPermission(activity: Activity, permission: String): PermissionStatus {
    val granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    if (granted) return PermissionStatus.Granted
    val rationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    return if (rationale) PermissionStatus.Denied else PermissionStatus.PermanentlyDenied
}

internal fun openAppSettings(activity: Activity) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", activity.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    activity.startActivity(intent)
}
