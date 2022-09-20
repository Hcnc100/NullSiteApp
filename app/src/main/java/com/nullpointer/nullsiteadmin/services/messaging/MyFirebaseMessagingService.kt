package com.nullpointer.nullsiteadmin.services.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepository
import com.nullpointer.nullsiteadmin.models.EmailContact
import com.nullpointer.nullsiteadmin.models.EmailDeserializer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val job = SupervisorJob()

    private val notifyHelper by lazy {
        NotifyMessagingHelper(this)
    }
    private val gson by lazy {
        GsonBuilder().registerTypeAdapter(
            EmailContact::class.java, EmailDeserializer()
        ).create()
    }

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(job).launch(Dispatchers.IO) {
            try {
                authRepository.updateTokenUser(token = token)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NullPointerException -> Timber.e("Error upload, user is maybe null")
                    else -> Timber.e("Error update token $e")
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        CoroutineScope(job).launch(Dispatchers.IO) {
            try {
                val email = gson.fromJson(message.data["notify"], EmailContact::class.java)
                notifyHelper.showNotifyForMessage(email)
            } catch (e: Exception) {
                Timber.e("Error to notify $e")
            }
        }
    }

}