package org.example.project.platform

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private class SharedPrefsStorage(private val prefs: SharedPreferences) : KeyValueStorage {
    override fun get(key: String): String? = prefs.getString(key, null)
    override fun put(key: String, value: String?) {
        prefs.edit().apply {
            if (value == null) remove(key) else putString(key, value)
        }.apply()
    }
}

@Composable
actual fun rememberKeyValueStorage(): KeyValueStorage {
    val app = LocalContext.current.applicationContext
    return remember(app) {
        SharedPrefsStorage(app.getSharedPreferences("pet_routine", Context.MODE_PRIVATE))
    }
}
