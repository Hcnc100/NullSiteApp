package com.nullpointer.nullsiteadmin.domain.email

import com.nullpointer.nullsiteadmin.models.email.EmailData
import kotlinx.coroutines.flow.Flow

interface EmailsRepository {
    val listEmails: Flow<List<EmailData>>
    suspend fun markAsOpen(idEmail: String)
    suspend fun deleterEmail(idEmail: String)
    suspend fun requestLastEmail(forceRefresh: Boolean): Int
    suspend fun concatenateEmails(): Int

}