package com.nullpointer.nullsiteadmin.domain.email

import com.nullpointer.nullsiteadmin.models.email.EmailContact
import kotlinx.coroutines.flow.Flow

interface EmailsRepository {
    val listEmails: Flow<List<EmailContact>>
    suspend fun markAsOpen(idEmail: String)
    suspend fun deleterEmail(idEmail: String)
    suspend fun requestLastEmail(forceRefresh: Boolean): Int
    suspend fun concatenateEmails(): Int

}