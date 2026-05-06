package org.example.project.platform

import androidx.compose.runtime.Composable

interface KeyValueStorage {
    fun get(key: String): String?
    fun put(key: String, value: String?)
}

@Composable
expect fun rememberKeyValueStorage(): KeyValueStorage
