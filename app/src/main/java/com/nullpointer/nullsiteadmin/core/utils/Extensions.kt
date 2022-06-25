package com.nullpointer.nullsiteadmin.core.utils

import android.content.Context
import android.text.format.DateFormat.is24HourFormat
import java.text.SimpleDateFormat
import java.util.*

fun Date?.toFormat(context: Context): String {
    val pattern = "EEEE dd/MM/yyyy HH:mm:ss".let {
        if (is24HourFormat(context)) it else it.plus(" aa")
    }
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    val dateTimeNow = this ?: Date()
    return format.format(dateTimeNow)
}