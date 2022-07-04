package com.nullpointer.nullsiteadmin.core.utils

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.text.format.DateFormat.is24HourFormat
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
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

fun Context.getNotifyServices(): NotificationManager {
    return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

fun Context.showToastMessage(@StringRes stringRes:Int){
    Toast.makeText(this,getString(stringRes), Toast.LENGTH_SHORT).show()
}