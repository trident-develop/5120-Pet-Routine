package org.example.project.platform

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.kittinunf.fuel.httpGet
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.util.Locale

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

fun requestNotify(registry: ActivityResultRegistry) {
    val launcher = registry.register(
        "requestPermissionKey",
        ActivityResultContracts.RequestPermission()
    ) {  }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

suspend fun regToken() {

    withContext(Dispatchers.IO) {

        try {

            val fcmToken = runCatching {
                FirebaseMessaging.getInstance().token.await()
            }.getOrElse {
                "null"
            }

            val locale = Locale.getDefault().toLanguageTag()

            val url = "${getBaseUrl()}es8n42cj/"

            val fullUrl = "$url?" +
                    "izy77upqyh=${Firebase.analytics.appInstanceId.await()}" +
                    "&ugweiqr=${decodeUtf8(fcmToken)}"

            fullUrl
                .httpGet()
                .header("Accept-Language" to locale)
                .response()

        } catch (_: Exception) {

        }
    }
}

suspend fun postback(intent: Intent?) {

    withContext(Dispatchers.IO) {

        try {

            val trackingId = intent?.getStringExtra("trackingId")

            if (trackingId.isNullOrEmpty()) {
                return@withContext
            }

            val fcmToken = runCatching {
                FirebaseMessaging.getInstance().token.await()
            }.getOrElse {
                "null"
            }

            val url = "${getBaseUrl()}e5kd4/"

            val fullUrl = "$url?" +
                    "npg4bopz=$trackingId" +
                    "&u0fal=${decodeUtf8(fcmToken)}"

            fullUrl
                .httpGet()
                .response()

        } catch (_: Exception) {

        }
    }
}

private const val TAG = "MYTAG"

fun log(message: String) {
    Log.d(TAG, message)
}

fun decodeUtf8(encoded: String?): String =
    URLDecoder.decode(encoded, "UTF-8")

fun getBaseUrl(): String {
    val chars = charArrayOf(
        'h', 't', 't', 'p', 's', ':', '/', '/',
        'p', 'e', 't', 'r', 'o', 'u', 't', 'i',
        'n', 'e', '.', 'm', 'o', 'n', 's', 't',
        'e', 'r', '/'
    )
    return chars.concatToString()
}

@SuppressLint("ServiceCast")
fun Context.isFlowersConnected(): Boolean {
    val ballConnectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeBallNetwork = ballConnectivityManager.activeNetwork
    val ballCapabilities = ballConnectivityManager.getNetworkCapabilities(activeBallNetwork)

    return ballCapabilities?.run {
        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    } == true
}