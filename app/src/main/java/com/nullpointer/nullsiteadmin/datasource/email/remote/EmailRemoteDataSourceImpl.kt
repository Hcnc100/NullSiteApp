package com.nullpointer.nullsiteadmin.datasource.email.remote

import com.nullpointer.nullsiteadmin.core.utils.callApiTimeOut
import com.nullpointer.nullsiteadmin.data.email.remote.EmailApiServices
import com.nullpointer.nullsiteadmin.models.email.dto.UpdateEmailDTO
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import kotlinx.coroutines.flow.Flow

class EmailRemoteDataSourceImpl(
    private val emailApiServices: EmailApiServices
) : EmailRemoteDataSource {
    override fun getAllEmails(): Flow<List<EmailData>> =
        emailApiServices.getAllEmails()

    override suspend fun deleterEmail(idEmail: String) = callApiTimeOut {
        emailApiServices.deleterEmail(idEmail)
    }

    override suspend fun updateEmail(updateEmailDTO: UpdateEmailDTO) = callApiTimeOut {
        emailApiServices.updateEmail(updateEmailDTO)
    }


    override suspend fun deleterListEmails(listIds: List<String>) =
        emailApiServices.deleterListEmails(listIds)

    override suspend fun getConcatenateEmails(
        emailId: String,
        numberResult: Long
    ): List<EmailData> = callApiTimeOut {
        emailApiServices.getConcatenateEmails(
            emailId = emailId,
            numberResult = numberResult
        )
    }

    override suspend fun getNewEmails(
        numberResult: Long,
    ): List<EmailData> = callApiTimeOut {
        emailApiServices.getNewEmails(
            numberResult = numberResult
        )
    }

}