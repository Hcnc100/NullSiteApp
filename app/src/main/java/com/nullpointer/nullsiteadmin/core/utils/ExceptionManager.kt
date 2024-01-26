package com.nullpointer.nullsiteadmin.core.utils

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.crashlytics.ktx.setCustomKeys
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.BuildConfig
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.exception.NullAppException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.tasks.await
import timber.log.Timber

object ExceptionManager {
    private val crash = Firebase.crashlytics
    private suspend fun getMessageForException(
        throwable: Throwable,
        messageToUser: Int? = null,
        debugMessage: String? = null
    ): Int {
        return when (throwable) {
            is NullAppException -> {
                when (throwable) {
                    is NullAppException.ServerTimeOut -> R.string.error_time_out
                    is NullAppException.NoInternetConnection -> R.string.error_network
                    is NullAppException.AuthException.Authenticated -> R.string.error_authenticated
                    is NullAppException.AuthException.UserNotFound -> R.string.error_user_not_found
                }
            }

            else -> {
                debugMessage?.let { crash.setCustomKeys { "debugMessage" to it } }
                crash.recordException(throwable)
                if (BuildConfig.DEBUG) {
                    Timber.e(throwable, debugMessage)
                    crash.checkForUnsentReports().await()
                }
                messageToUser ?: R.string.error_unknown
            }
        }
    }

    suspend fun sendMessageErrorToException(
        exception: Exception,
        channel: Channel<Int>,
        messageResource: Int? = null,
        debugMessage: String? = null
    ) {
        val messageUser = getMessageForException(exception, messageResource, debugMessage)
        channel.trySend(messageUser)
    }

//    fun getMessageForException(throwable: Throwable, message: String): Int {
//        return getMessageForException(Exception(throwable), message)
//    }
//
//    private fun getMessageForExceptionAuth(exception: Exception): Int? {
//        return when (exception) {
//            is FirebaseAuthInvalidCredentialsException -> R.string.error_authenticated
//            is FirebaseAuthInvalidUserException -> R.string.error_user_not_found
//            else -> null
//        }
//    }
//
//    private fun getMessageForMsgException(message: String?, exception: Exception): Int {
//        return when (message) {
//            NO_INTERNET_CONNECTION -> R.string.error_network
//            SERVER_TIME_OUT -> R.string.error_time_out
//            else -> {
//                crash.recordException(exception)
//                R.string.error_unknown
//            }
//        }
//    }
//
//    fun getMessageForException(exception: Exception, message: String): Int {
//        Timber.e("${message}: $exception")
//        return getMessageForExceptionAuth(exception) ?: getMessageForMsgException(
//            exception.message,
//            exception = exception
//        )
//    }
//

}