package com.nullpointer.nullsiteadmin.domain.email

import com.nullpointer.nullsiteadmin.core.utils.callApiTimeOut
import com.nullpointer.nullsiteadmin.datasource.email.local.EmailLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.email.remote.EmailRemoteDataSource
import com.nullpointer.nullsiteadmin.models.dto.UpdateEmailDTO
import com.nullpointer.nullsiteadmin.models.email.EmailData
import kotlinx.coroutines.flow.Flow

class EmailsRepoImpl(
    private val emailRemoteDataSource: EmailRemoteDataSource,
    private val emailLocalDataSource: EmailLocalDataSource
) : EmailsRepository {

    companion object {
        private const val SIZE_REQUEST_EMAIL = 20L
        private const val SIZE_CONCATENATE_EMAIL = 15L
    }

    override val listEmails: Flow<List<EmailData>> = emailLocalDataSource.listEmail

    override suspend fun deleterEmail(idEmail: String) {
        emailRemoteDataSource.deleterEmail(idEmail)
        emailLocalDataSource.deleteEmail(idEmail)
    }

    override suspend fun markAsOpen(emailData: EmailData) {
        val updateEmailDTO = UpdateEmailDTO.fromEmailData(emailData)
        emailRemoteDataSource.updateEmail(updateEmailDTO)

        val updateEmailData= emailData.copy(isOpen = true)
        emailLocalDataSource.updateEmail(updateEmailData)
    }

    override suspend fun requestLastEmail(forceRefresh: Boolean): Int {
        val email = emailLocalDataSource.getMoreRecentEmail()
        val idEmail = if (forceRefresh) null else email?.idEmail
        val newEmails = emailRemoteDataSource.getNewEmails(
                numberResult = SIZE_REQUEST_EMAIL,
                includeEmail = false,
                emailId = idEmail
            )

        emailLocalDataSource.updateAllEmails(newEmails)
        return newEmails.size
    }

    override suspend fun concatenateEmails(): Int {
        val lastEmail = emailLocalDataSource.getLastEmail()
        return if (lastEmail != null) {
            val newEmails = emailRemoteDataSource.getConcatenateEmails(
                    emailId = lastEmail.idEmail,
                    includeEmail = false,
                    numberResult = SIZE_CONCATENATE_EMAIL
                )

            emailLocalDataSource.insertListEmails(newEmails)
            return newEmails.size
        } else {
            0
        }
    }
}