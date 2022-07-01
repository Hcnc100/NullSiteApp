package com.nullpointer.nullsiteadmin.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepository
import com.nullpointer.nullsiteadmin.models.EmailContact
import com.nullpointer.nullsiteadmin.models.EmailDeserializer
import com.nullpointer.nullsiteadmin.ui.activitys.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val ID_CHANNEL_MESSAGE = "ID_CHANNEL_MESSAGE"
        private const val NAME_CHANNEL_MESSAGE = "NAME_CHANNEL_MESSAGE"
    }

    private val job = SupervisorJob()
    private val notifyHelper by lazy {
        NotificationHelper()
    }
    private val gson by lazy {
        GsonBuilder().registerTypeAdapter(
            EmailContact::class.java, EmailDeserializer()
        ).create()
    }

    @Inject
    lateinit var authRepoImpl: AuthRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(job).launch {
            try {
                authRepoImpl.updateTokenUser(token = token)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NullPointerException -> Timber.e("Error al actualizar el toke, el usuario posioblemente no esta autenticado")
                    else -> Timber.e("Error desconocido al actializar token $e")
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        CoroutineScope(job).launch {
            try {
                val email = gson.fromJson(message.data["notify"], EmailContact::class.java)
                notifyHelper.showNotifyForMessage(email)
            } catch (e: Exception) {
                Timber.e("Error al notificar $e")
            }
        }
    }

    private inner class NotificationHelper {

        private val context get() = this@MyFirebaseMessagingService
        private val notifyManager get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        private val baseNotify by lazy { createBaseNotify() }

        init {
            createNotificationChannel()
        }

        private fun createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // * if is needed create channel notification
                val channel = NotificationChannel(
                    ID_CHANNEL_MESSAGE,
                    NAME_CHANNEL_MESSAGE,
                    NotificationManager.IMPORTANCE_HIGH
                )
                notifyManager.createNotificationChannel(channel)
            }
        }

        private fun getPendingIntentCompose(): PendingIntent? {
            // * create deep link
            // * this go to post for notification
            val deepLinkIntent = Intent(
                Intent.ACTION_VIEW,
                "https://www.nullsiteadmin.com/contact/".toUri(),
                context,
                MainActivity::class.java
            )
            // * create pending intent compose
            val deepLinkPendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(deepLinkIntent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            return deepLinkPendingIntent
        }

        private fun createBaseNotify(): NotificationCompat.Builder {
            return NotificationCompat.Builder(
                context,
                ID_CHANNEL_MESSAGE
            ).setAutoCancel(true)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_safe)
        }

        fun showNotifyForMessage(email: EmailContact) {
            val baseNotify = this.baseNotify.apply {
                setContentTitle("Nuevo Email")
                    .setContentText(email.message)
                    .setContentIntent(
                        getPendingIntentCompose()
                    )
            }
            notifyManager.notify(0, baseNotify.build())
        }
    }
}