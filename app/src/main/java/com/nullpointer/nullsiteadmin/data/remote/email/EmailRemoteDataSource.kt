package com.nullpointer.nullsiteadmin.data.remote.email

import com.nullpointer.nullsiteadmin.models.email.EmailContact
import kotlinx.coroutines.flow.Flow

interface EmailRemoteDataSource {
    fun getAllEmails(): Flow<List<EmailContact>>
    suspend fun deleterEmail(idEmail: String)
    suspend fun markAsOpen(idEmail: String)
    suspend fun deleterListEmails(listIds: List<String>)
    suspend fun getConcatenateEmails(
        includeEmail: Boolean, emailId: String? = null, numberResult: Long
    ): List<EmailContact>

    suspend fun getNewEmails(
        includeEmail: Boolean, numberResult: Long, emailId: String?
    ): List<EmailContact>
}