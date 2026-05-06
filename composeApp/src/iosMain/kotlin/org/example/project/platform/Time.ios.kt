package org.example.project.platform

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

private fun formatter(pattern: String): NSDateFormatter =
    NSDateFormatter().apply {
        dateFormat = pattern
        locale = NSLocale.currentLocale
    }

actual fun nowDate(): String = formatter("yyyy-MM-dd").stringFromDate(NSDate())

actual fun nowTime(): String = formatter("HH:mm").stringFromDate(NSDate())
