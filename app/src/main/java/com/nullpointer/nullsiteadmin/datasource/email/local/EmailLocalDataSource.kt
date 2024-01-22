package com.nullpointer.nullsiteadmin.datasource.email.local

import com.nullpointer.nullsiteadmin.models.email.EmailData
import kotlinx.coroutines.flow.Flow

interface EmailLocalDataSource {
    val listEmail: Flow<List<EmailData>>

    suspend fun updateAllEmails(listIdEmails: List<EmailData>)
    suspend fun insertListEmails(listEmails: List<EmailData>)
    suspend fun deleteListEmails(listIdEmails: List<String>)
    suspend fun getEmailById(idEmail: String): EmailData?
    suspend fun getMoreRecentEmail(): EmailData?
    suspend fun updateEmail(email: EmailData)
    suspend fun getLastEmail(): EmailData?
    suspend fun deleteEmail(idEmail: String)
    suspend fun deleterAllEmails()
}