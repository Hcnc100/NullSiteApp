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
import com.nullpointer.nullsiteadmin.core.utils.correctFlag
import com.nullpointer.nullsiteadmin.core.utils.getNotifyServices
import com.nullpointer.nullsiteadmin.data.services.ServicesManager.Companion.STOP_COMMAND
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
                context.getString(R.string.name_channel_upload),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPendingIntentStop(): PendingIntent =
        PendingIntent.getService(
            context,
            1,
            Intent(context, UploadImageServices::class.java).apply { action = STOP_COMMAND },
            context.correctFlag
        )

    private fun getPendingIntentToClick(): PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java),
        context.correctFlag
    )

    private fun getNotificationUpload() =
        NotificationCompat.Builder(context, ID_CHANNEL_UPLOAD_IMG)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_safe)
            .setContentTitle(context.getString(R.string.title_upload_notify))
            .setContentIntent(getPendingIntentToClick())
            .addAction(R.drawable.ic_stop,
                context.getString(R.string.name_Action_stop),
                getPendingIntentStop())

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



