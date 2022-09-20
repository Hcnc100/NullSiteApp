package com.nullpointer.nullsiteadmin.domain.email

import com.nullpointer.nullsiteadmin.models.EmailContact
import kotlinx.coroutines.flow.Flow

interface EmailsRepository {
    val listEmails: Flow<List<EmailContact>>
    suspend fun deleterEmail(idEmail: String)
    suspend fun markAsOpen(idEmail: String)
}