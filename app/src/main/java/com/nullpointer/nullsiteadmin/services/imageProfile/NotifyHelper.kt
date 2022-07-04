package com.nullpointer.nullsiteadmin.services.imageProfile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.utils.getNotifyServices
import com.nullpointer.nullsiteadmin.services.imageProfile.UploadImageServicesControl.STOP_COMMAND
import com.nullpointer.nullsiteadmin.ui.activitys.MainActivity

class NotifyUploadImgHelper(private val context: Context) {
    companion object {
        const val ID_NOTIFY_UPLOAD_IMG = 1234
        const val ID_CHANNEL_UPLOAD_IMG = "ID_CHANNEL_UPLOAD_IMG"
    }

    private val notificationManager = context.getNotifyServices()
    private val baseNotifyUpload by lazy { getNotificationUpload() }

    init {
        createChannelNotification()
    }

    private fun createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // * if is needed create channel notification
            val channel = NotificationChannel(
                ID_CHANNEL_UPLOAD_IMG,
                "Canal de subidad",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPendingIntentStop(): PendingIntent =
        PendingIntent.getService(
            context, 1,
            Intent(context, UploadImageServices::class.java).apply {
                action = STOP_COMMAND
            }, PendingIntent.FLAG_IMMUTABLE
        )

    private fun getPendingIntentToClick(): PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    private fun getNotificationUpload() =
        NotificationCompat.Builder(context, ID_CHANNEL_UPLOAD_IMG)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_safe)
            .setContentTitle("Subiendo...")
            .setContentIntent(getPendingIntentToClick())
            .addAction(R.drawable.ic_stop, "Stop", getPendingIntentStop())

    fun startServicesForeground(service: Service){
        service.startForeground(ID_NOTIFY_UPLOAD_IMG,baseNotifyUpload.build())
    }

    fun updateNotifyFinishUpdate() {
        baseNotifyUpload.setProgress(0,0,true)
        notificationManager.notify(ID_NOTIFY_UPLOAD_IMG, baseNotifyUpload.build())
    }

    fun updateNotifyProgressUpload(progress: Int){
        baseNotifyUpload.setProgress(100,progress,false)
        notificationManager.notify(ID_NOTIFY_UPLOAD_IMG, baseNotifyUpload.build())
    }

}

