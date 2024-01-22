package com.nullpointer.nullsiteadmin.domain.email

import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import kotlinx.coroutines.flow.Flow

interface EmailsRepository {
    val listEmails: Flow<List<EmailData>>
    suspend fun markAsOpen(emailData: EmailData)
    suspend fun deleterEmail(idEmail: String)
    suspend fun requestLastEmail(): Int
    suspend fun concatenateEmails(): Int

}