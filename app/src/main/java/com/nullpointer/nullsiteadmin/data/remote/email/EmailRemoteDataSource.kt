package com.nullpointer.nullsiteadmin.data.remote.email

import com.nullpointer.nullsiteadmin.models.email.EmailContact
import kotlinx.coroutines.flow.Flow

interface EmailRemoteDataSource {
    fun getAllEmails(): Flow<List<EmailContact>>
    suspend fun deleterEmail(idEmail: String)
    suspend fun markAsOpen(idEmail: String)
    suspend fun getNewEmails(
        includeStart: Boolean, startWithId: String? = null, numberResult: Long
    ): List<EmailContact>

    suspend fun getLastEmails(
        includeEnd: Boolean, endWithId: String? = null, numberResult: Long
    ): List<EmailContact>
}