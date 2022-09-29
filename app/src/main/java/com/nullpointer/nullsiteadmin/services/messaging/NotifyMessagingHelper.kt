package com.nullpointer.nullsiteadmin.services.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.utils.correctFlag
import com.nullpointer.nullsiteadmin.core.utils.getNotifyServices
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import com.nullpointer.nullsiteadmin.ui.activitys.MainActivity
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EmailDetailsScreenDestination

class NotifyMessagingHelper(private val context: Context) {
    companion object {
        private const val ID_CHANNEL_MESSAGE = "ID_CHANNEL_MESSAGE_12345"
        private const val ID_GROUP_MESSAGE = "ID_GROUP_NULL_ADMIN_APP"
    }

    private val notifyManager = context.getNotifyServices()

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // * if is needed create channel notification
            val channel = NotificationChannel(
                ID_CHANNEL_MESSAGE,
                context.getString(R.string.name_channel_contact),
                NotificationManager.IMPORTANCE_HIGH
            )
            notifyManager.createNotificationChannel(channel)
        }
    }

    private fun getPendingIntentCompose(email: EmailContact): PendingIntent? {
        // * create deep link
        // * this go to post for notificaation
        val route= EmailDetailsScreenDestination(email).route

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "https://www.nullsiteadmin.com/$route".toUri(),
            context,
            MainActivity::class.java
        )
        // * create pending intent compose
        val deepLinkPendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, context.correctFlag)
        }
        return deepLinkPendingIntent
    }

    private fun createBaseNotify(): NotificationCompat.Builder {
        return NotificationCompat.Builder(
            context,
            ID_CHANNEL_MESSAGE
        ).setAutoCancel(true)
            .setOngoing(false)
            .setSmallIcon(R.drawable.ic_email_recv)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroup(ID_GROUP_MESSAGE)
    }

    fun showNotifyForMessage(email: EmailContact) {
        val baseNotify = createBaseNotify().apply {
            setContentIntent(getPendingIntentCompose(email))
            setContentTitle(context.getString(R.string.title_notify_new_email))
            setContentText(context.getString(R.string.text_to_email, email.email))
            setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(email.toFullMessage())
            )
        }
        notifyManager.notify(email.idEmail.hashCode(), baseNotify.build())
    }
}

private fun EmailContact.toFullMessage(): String {
    return "$email\n($name)\n\n$subject\n\n$message"
}