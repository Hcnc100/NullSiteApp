package com.nullpointer.nullsiteadmin.datasource.email.remote

import com.nullpointer.nullsiteadmin.models.email.EmailData
import kotlinx.coroutines.flow.Flow

interface EmailRemoteDataSource {
    fun getAllEmails(): Flow<List<EmailData>>
    suspend fun deleterEmail(idEmail: String)
    suspend fun markAsOpen(idEmail: String)
    suspend fun deleterListEmails(listIds: List<String>)
    suspend fun getConcatenateEmails(
        includeEmail: Boolean, emailId: String? = null, numberResult: Long
    ): List<EmailData>

    suspend fun getNewEmails(
        includeEmail: Boolean, numberResult: Long, emailId: String?
    ): List<EmailData>
}