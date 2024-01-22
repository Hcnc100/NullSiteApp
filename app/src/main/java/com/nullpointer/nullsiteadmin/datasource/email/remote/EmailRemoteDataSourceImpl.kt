package com.nullpointer.nullsiteadmin.datasource.email.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.core.utils.awaitAll
import com.nullpointer.nullsiteadmin.core.utils.callApiTimeOut
import com.nullpointer.nullsiteadmin.core.utils.getConcatenateObjects
import com.nullpointer.nullsiteadmin.core.utils.getNewObjects
import com.nullpointer.nullsiteadmin.core.utils.getTimeEstimate
import com.nullpointer.nullsiteadmin.data.email.remote.EmailApiServices
import com.nullpointer.nullsiteadmin.models.dto.UpdateEmailDTO
import com.nullpointer.nullsiteadmin.models.email.EmailData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Date

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