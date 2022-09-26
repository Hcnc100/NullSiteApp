package com.nullpointer.nullsiteadmin.core.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object InternetCheck {
    suspend fun isNetworkAvailable() = coroutineScope {
        return@coroutineScope try {
            val sock = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)
            withContext(Dispatchers.IO) {
                sock.connect(socketAddress, 1500)
                sock.close()
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}