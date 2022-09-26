package com.nullpointer.nullsiteadmin.core.utils

import com.nullpointer.nullsiteadmin.R
import kotlinx.coroutines.channels.Channel
import timber.log.Timber

object ExceptionManager {
    const val NO_INTERNET_CONNECTION = "NO_INTERNET_NETWORK"
    const val SERVER_TIME_OUT = "SERVER_TIME_OUT"

    fun getMessageForException(throwable: Throwable, message: String): Int {
        return getMessageForException(Exception(throwable), message)
    }

    fun getMessageForException(exception: Exception, message: String): Int {
        Timber.e("${message}: $exception")
        return when (exception.message) {
            NO_INTERNET_CONNECTION -> R.string.error_network
            SERVER_TIME_OUT -> R.string.error_time_out
            else -> R.string.error_unknown
        }
    }

    fun sendMessageErrorToException(exception: Exception, message: String, channel: Channel<Int>) {
        val messageUser = getMessageForException(exception, message)
        channel.trySend(messageUser)
    }
}