package org.example.project.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSUserDefaults

private class NSUserDefaultsStorage : KeyValueStorage {
    private val defaults = NSUserDefaults.standardUserDefaults
    override fun get(key: String): String? = defaults.stringForKey(key)
    override fun put(key: String, value: String?) {
        if (value == null) defaults.removeObjectForKey(key)
        else defaults.setObject(value, forKey = key)
    }
}

@Composable
actual fun rememberKeyValueStorage(): KeyValueStorage =
    remember { NSUserDefaultsStorage() }
