package com.nullpointer.nullsiteadmin.data.local.email

import com.nullpointer.nullsiteadmin.models.email.EmailContact
import kotlinx.coroutines.flow.Flow

interface EmailLocalDataSource {
    val listEmail: Flow<List<EmailContact>>

    suspend fun updateAllEmails(listEmails: List<EmailContact>)
    suspend fun deleteListEmails(listEmails: List<EmailContact>)
    suspend fun deleteEmail(email: EmailContact)
    suspend fun deleterAllEmails()
}