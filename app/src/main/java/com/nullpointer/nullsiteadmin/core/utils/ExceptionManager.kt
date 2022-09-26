package com.nullpointer.nullsiteadmin.core.utils

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.nullpointer.nullsiteadmin.R
import kotlinx.coroutines.channels.Channel
import timber.log.Timber

object ExceptionManager {
    const val NO_INTERNET_CONNECTION = "NO_INTERNET_NETWORK"
    const val SERVER_TIME_OUT = "SERVER_TIME_OUT"

    fun getMessageForException(throwable: Throwable, message: String): Int {
        return getMessageForException(Exception(throwable), message)
    }

    private fun getMessageForExceptionAuth(exception: Exception): Int? {
        return when (exception) {
            is FirebaseAuthInvalidCredentialsException -> R.string.error_authenticated
            is FirebaseAuthInvalidUserException -> R.string.error_user_not_found
            else -> null
        }
    }

    private fun getMessageForMsgException(message: String?): Int {
        return when (message) {
            NO_INTERNET_CONNECTION -> R.string.error_network
            SERVER_TIME_OUT -> R.string.error_time_out
            else -> R.string.error_unknown
        }
    }

    fun getMessageForException(exception: Exception, message: String): Int {
        Timber.e("${message}: $exception")
        return getMessageForExceptionAuth(exception) ?: getMessageForMsgException(exception.message)
    }

    fun sendMessageErrorToException(exception: Exception, message: String, channel: Channel<Int>) {
        val messageUser = getMessageForException(exception, message)
        channel.trySend(messageUser)
    }
}