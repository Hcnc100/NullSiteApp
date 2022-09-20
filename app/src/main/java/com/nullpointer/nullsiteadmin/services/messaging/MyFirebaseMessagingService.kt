package com.nullpointer.nullsiteadmin.services.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepository
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import com.nullpointer.nullsiteadmin.models.email.EmailDeserializer
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
        createGsonBuilder()
    }


    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate() {
        super.onCreate()
        safeLaunchTokenOperation {
            authRepository.verifyTokenMessaging()
        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        safeLaunchTokenOperation {
            authRepository.updateTokenUser(token = token)
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

    private fun createGsonBuilder(): Gson {
        return GsonBuilder().registerTypeAdapter(
            EmailContact::class.java, EmailDeserializer()
        ).create()
    }

    private fun safeLaunchTokenOperation(
        callToken: suspend () -> Unit
    ) {
        CoroutineScope(job).launch(Dispatchers.IO) {
            try {
                callToken()
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NullPointerException -> Timber.e("Error upload, user is maybe null")
                    else -> Timber.e("Unknown error token services $e")
                }
            }
        }
    }

}