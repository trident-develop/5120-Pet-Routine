package org.example.project.platform

import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSURL
import platform.Foundation.NSOperationQueue
import platform.Photos.PHAccessLevelReadWrite
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

enum class IOSPermissionStatus { NotDetermined, Authorized, Denied }

enum class IOSPhotoLibraryStatus { NotDetermined, FullAccess, LimitedAccess, Denied }

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

internal fun checkPhotoLibraryStatus(): IOSPhotoLibraryStatus {
    return when (PHPhotoLibrary.authorizationStatusForAccessLevel(PHAccessLevelReadWrite)) {
        PHAuthorizationStatusAuthorized -> IOSPhotoLibraryStatus.FullAccess
        PHAuthorizationStatusLimited -> IOSPhotoLibraryStatus.LimitedAccess
        PHAuthorizationStatusNotDetermined -> IOSPhotoLibraryStatus.NotDetermined
        else -> IOSPhotoLibraryStatus.Denied
    }
}

internal fun requestPhotoLibraryAccess(onResult: (IOSPhotoLibraryStatus) -> Unit) {
    PHPhotoLibrary.requestAuthorizationForAccessLevel(PHAccessLevelReadWrite) { status ->
        runOnMain {
            val mapped = when (status) {
                PHAuthorizationStatusAuthorized -> IOSPhotoLibraryStatus.FullAccess
                PHAuthorizationStatusLimited -> IOSPhotoLibraryStatus.LimitedAccess
                PHAuthorizationStatusNotDetermined -> IOSPhotoLibraryStatus.NotDetermined
                else -> IOSPhotoLibraryStatus.Denied
            }
            onResult(mapped)
        }
    }
}

internal fun openIOSAppSettings() {
    val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return
    runOnMain {
        UIApplication.sharedApplication.openURL(
            url,
            options = mapOf<Any?, Any>(),
            completionHandler = null,
        )
    }
}

internal fun runOnMain(block: () -> Unit) {
    NSOperationQueue.mainQueue.addOperationWithBlock(block)
}
