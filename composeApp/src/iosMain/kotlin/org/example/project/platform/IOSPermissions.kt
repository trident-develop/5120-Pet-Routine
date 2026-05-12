package org.example.project.platform

import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSOperationQueue

enum class IOSPermissionStatus { NotDetermined, Authorized, Denied }

internal fun checkCameraStatus(): IOSPermissionStatus {
    return when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
        AVAuthorizationStatusAuthorized -> IOSPermissionStatus.Authorized
        AVAuthorizationStatusNotDetermined -> IOSPermissionStatus.NotDetermined
        AVAuthorizationStatusDenied,
        AVAuthorizationStatusRestricted -> IOSPermissionStatus.Denied
        else -> IOSPermissionStatus.Denied
    }
}

internal fun requestCameraAccess(onResult: (IOSPermissionStatus) -> Unit) {
    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
        runOnMain {
            onResult(if (granted) IOSPermissionStatus.Authorized else IOSPermissionStatus.Denied)
        }
    }
}

internal fun runOnMain(block: () -> Unit) {
    NSOperationQueue.mainQueue.addOperationWithBlock(block)
}
