package com.nullpointer.nullsiteadmin.core.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.format.DateFormat.is24HourFormat
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

val Context.correctFlag:Int get() {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
}

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

fun Context.showToastMessage(@StringRes stringRes: Int) {
    Toast.makeText(this, getString(stringRes), Toast.LENGTH_SHORT).show()
}

fun Context.sendEmail(email:String){
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$email"))
    startActivity(intent)
}

@Composable
inline fun <reified VM : ViewModel> shareViewModel():VM {
    val activity= LocalContext.current as ComponentActivity
    return hiltViewModel(activity)
}