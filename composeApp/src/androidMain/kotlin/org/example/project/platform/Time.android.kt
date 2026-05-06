package org.example.project.platform

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun nowDate(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

actual fun nowTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
