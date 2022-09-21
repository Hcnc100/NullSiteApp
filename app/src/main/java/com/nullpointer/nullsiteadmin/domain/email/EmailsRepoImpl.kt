package com.nullpointer.nullsiteadmin.domain.email

import com.nullpointer.nullsiteadmin.data.local.email.EmailLocalDataSource
import com.nullpointer.nullsiteadmin.data.remote.email.EmailRemoteDataSource
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import kotlinx.coroutines.flow.Flow

class EmailsRepoImpl(
    private val emailRemoteDataSource: EmailRemoteDataSource,
    private val emailLocalDataSource: EmailLocalDataSource
) : EmailsRepository {

    companion object {
        private const val SIZE_REQUEST_EMAIL = 10L
        private const val SIZE_CONCATENATE_EMAIL = 5L
    }

    override val listEmails: Flow<List<EmailContact>> = emailLocalDataSource.listEmail

    override suspend fun deleterEmail(idEmail: String) {
        emailRemoteDataSource.deleterEmail(idEmail)
        emailLocalDataSource.deleteEmail(idEmail)
    }

    override suspend fun markAsOpen(idEmail: String) {
        emailRemoteDataSource.markAsOpen(idEmail)
        emailLocalDataSource.getEmailById(idEmail)?.let {
            emailLocalDataSource.updateEmail(it.copy(isOpen = true))
        }
    }

    override suspend fun requestLastEmail(forceRefresh: Boolean): Int {
        val email = emailLocalDataSource.getMoreRecentEmail()
        val idEmail = if (forceRefresh) null else email?.id
        val newEmails = emailRemoteDataSource.getLastEmails(
            numberResult = SIZE_REQUEST_EMAIL,
            includeEnd = false,
            endWithId = idEmail
        )
        if (newEmails.isNotEmpty()) emailLocalDataSource.updateAllEmails(newEmails)
        return newEmails.size
    }

    override suspend fun concatenateEmails(): Int {
        val lastEmail = emailLocalDataSource.getLastEmail()
        return if (lastEmail != null) {
            val newEmails = emailRemoteDataSource.getNewEmails(
                startWithId = lastEmail.id,
                includeStart = false,
                numberResult = SIZE_CONCATENATE_EMAIL
            )
            if (newEmails.isNotEmpty()) emailLocalDataSource.insertListEmails(newEmails)
            return newEmails.size
        } else {
            0
        }
    }
}