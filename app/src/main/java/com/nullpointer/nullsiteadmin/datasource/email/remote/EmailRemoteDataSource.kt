package com.nullpointer.nullsiteadmin.datasource.email.remote

import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import com.nullpointer.nullsiteadmin.models.email.dto.UpdateEmailDTO
import kotlinx.coroutines.flow.Flow

interface EmailRemoteDataSource {
    fun getAllEmails(): Flow<List<EmailData>>
    suspend fun deleterEmail(idEmail: String)
    suspend fun updateEmail(idEmail: String, updateEmailDTO: UpdateEmailDTO)
    suspend fun deleterListEmails(listIds: List<String>)
    suspend fun getConcatenateEmails(
         emailId: String,
         numberResult: Long

    ): List<EmailData>

    suspend fun getNewEmails(
        numberResult: Long
    ): List<EmailData>
}