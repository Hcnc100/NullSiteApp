package com.nullpointer.nullsiteadmin.domain.email

import com.nullpointer.nullsiteadmin.core.utils.callApiTimeOut
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
        callApiTimeOut { emailRemoteDataSource.deleterEmail(idEmail) }
        emailLocalDataSource.deleteEmail(idEmail)
    }

    override suspend fun markAsOpen(idEmail: String) {
        callApiTimeOut { emailRemoteDataSource.markAsOpen(idEmail) }
        emailLocalDataSource.getEmailById(idEmail)?.let {
            emailLocalDataSource.updateEmail(it.copy(isOpen = true))
        }
    }

    override suspend fun requestLastEmail(forceRefresh: Boolean): Int {
        val email = emailLocalDataSource.getMoreRecentEmail()
        val idEmail = if (forceRefresh) null else email?.idEmail
        val newEmails = callApiTimeOut {
            emailRemoteDataSource.getNewEmails(
                numberResult = SIZE_REQUEST_EMAIL,
                includeEmail = false,
                emailId = idEmail
            )
        }
        if (newEmails.isNotEmpty()) emailLocalDataSource.updateAllEmails(newEmails)
        return newEmails.size
    }

    override suspend fun concatenateEmails(): Int {
        val lastEmail = emailLocalDataSource.getLastEmail()
        return if (lastEmail != null) {
            val newEmails = callApiTimeOut {
                emailRemoteDataSource.getConcatenateEmails(
                    emailId = lastEmail.idEmail,
                    includeEmail = false,
                    numberResult = SIZE_CONCATENATE_EMAIL
                )
            }
            if (newEmails.isNotEmpty()) emailLocalDataSource.insertListEmails(newEmails)
            return newEmails.size
        } else {
            0
        }
    }
}