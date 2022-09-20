package com.nullpointer.nullsiteadmin.domain.email

import com.nullpointer.nullsiteadmin.data.remote.email.EmailDataSource
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import kotlinx.coroutines.flow.Flow

class EmailsRepoImpl(
    private val emailDataSource: EmailDataSource
) : EmailsRepository {
    override val listEmails: Flow<List<EmailContact>> = emailDataSource.getAllEmails()

    override suspend fun deleterEmail(idEmail: String) =
        emailDataSource.deleterEmail(idEmail)

    override suspend fun markAsOpen(idEmail: String) =
        emailDataSource.markAsOpen(idEmail)
}