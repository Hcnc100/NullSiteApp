package com.nullpointer.nullsiteadmin.datasource.email.remote

import com.nullpointer.nullsiteadmin.models.email.dto.UpdateEmailDTO
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import kotlinx.coroutines.flow.Flow

interface EmailRemoteDataSource {
    fun getAllEmails(): Flow<List<EmailData>>
    suspend fun deleterEmail(idEmail: String)
    suspend fun updateEmail(updateEmailDTO: UpdateEmailDTO)
    suspend fun deleterListEmails(listIds: List<String>)
    suspend fun getConcatenateEmails(
         emailId: String,
         numberResult: Long

    ): List<EmailData>

    suspend fun getNewEmails(
        numberResult: Long
    ): List<EmailData>
}