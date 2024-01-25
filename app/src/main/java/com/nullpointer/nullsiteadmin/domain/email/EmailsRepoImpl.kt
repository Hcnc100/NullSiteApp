package com.nullpointer.nullsiteadmin.domain.email

import com.nullpointer.nullsiteadmin.datasource.email.local.EmailLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.email.remote.EmailRemoteDataSource
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import com.nullpointer.nullsiteadmin.models.email.dto.UpdateEmailDTO
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
        emailRemoteDataSource.updateEmail(
            idEmail = emailData.idEmail,
            updateEmailDTO = updateEmailDTO
        )

        val updateEmailData = emailData.copy(isOpen = true)
        emailLocalDataSource.updateEmail(updateEmailData)
    }

    override suspend fun requestLastEmail(): Int {
        val newEmails = emailRemoteDataSource.getNewEmails(numberResult = SIZE_REQUEST_EMAIL)
        emailLocalDataSource.updateAllEmails(newEmails)
        return newEmails.size
    }

    override suspend fun concatenateEmails(): Int {
        val lastEmail = emailLocalDataSource.getLastEmail()
        return if (lastEmail != null) {
            val newEmails = emailRemoteDataSource.getConcatenateEmails(
                    emailId = lastEmail.idEmail,
                    numberResult = SIZE_CONCATENATE_EMAIL
                )
            emailLocalDataSource.insertListEmails(newEmails)
            return newEmails.size
        } else {
            0
        }
    }
}