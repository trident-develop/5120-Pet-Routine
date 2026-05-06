package org.example.project.platform

/** ISO-style "yyyy-MM-dd" date string for today, in the device's local time. */
expect fun nowDate(): String

/** Compact "HH:mm" time string for now. */
expect fun nowTime(): String
