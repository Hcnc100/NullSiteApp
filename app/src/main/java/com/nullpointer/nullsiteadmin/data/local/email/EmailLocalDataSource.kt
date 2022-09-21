package com.nullpointer.nullsiteadmin.data.local.email

import com.nullpointer.nullsiteadmin.models.email.EmailContact
import kotlinx.coroutines.flow.Flow

interface EmailLocalDataSource {
    val listEmail: Flow<List<EmailContact>>

    suspend fun updateAllEmails(listIdEmails: List<EmailContact>)
    suspend fun insertListEmails(listEmails: List<EmailContact>)
    suspend fun deleteListEmails(listIdEmails: List<String>)
    suspend fun getEmailById(idEmail: String): EmailContact?
    suspend fun getMoreRecentEmail(): EmailContact?
    suspend fun updateEmail(email: EmailContact)
    suspend fun getLastEmail(): EmailContact?
    suspend fun deleteEmail(idEmail: String)
    suspend fun deleterAllEmails()
}