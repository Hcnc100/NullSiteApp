package com.nullpointer.nullsiteadmin.services.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepository
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepository
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val job = SupervisorJob()

    private val notifyHelper by lazy { NotifyMessagingHelper(this) }


    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var emailRepository: EmailsRepository



    override fun onNewToken(token: String) {
        super.onNewToken(token)
        safeLaunchTokenOperation("Error update token when update token") {
            authRepository.verifyInfoPhoneData()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        safeLaunchTokenOperation("Error when process new message $message") {
            val email = EmailData.fromNotifyFirebase(message.data)
            notifyHelper.showNotifyForMessage(email)
            emailRepository.requestLastEmail()
        }
    }

    private fun safeLaunchTokenOperation(
        message: String,
        callToken: suspend () -> Unit
    ) {
        CoroutineScope(job).launch(Dispatchers.IO) {
            try {
                callToken()
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    else -> Timber.e("$message: $e")
                }
            }
        }
    }
}