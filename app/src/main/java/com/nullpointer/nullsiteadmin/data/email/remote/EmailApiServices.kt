package com.nullpointer.nullsiteadmin.data.email.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.core.utils.Constants
import com.nullpointer.nullsiteadmin.core.utils.awaitAll
import com.nullpointer.nullsiteadmin.core.utils.getConcatenateObjects
import com.nullpointer.nullsiteadmin.core.utils.getNewObjects
import com.nullpointer.nullsiteadmin.core.utils.getTimeEstimate
import com.nullpointer.nullsiteadmin.models.dto.UpdateEmailDTO
import com.nullpointer.nullsiteadmin.models.email.EmailData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class EmailApiServices {


    private val collectionEmail = Firebase.firestore.collection(Constants.EMAIL_COLLECTION)


    fun getAllEmails(): Flow<List<EmailData>> = callbackFlow {
        val listener = collectionEmail.orderBy(Constants.CREATE_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let { channel.close(it) }
                try {
                    val listEmails = value!!.documents.mapNotNull(::fromDocument)
                    trySend(listEmails)
                } catch (e: Exception) {
                    channel.close(e)
                }
            }
        awaitClose { listener.remove() }
    }


    suspend fun getConcatenateEmails(
        includeEmail: Boolean,
        emailId: String?,
        numberResult: Long
    ): List<EmailData> {
        return collectionEmail.getConcatenateObjects(
            nResults = numberResult,
            startWithId = emailId,
            transform = ::fromDocument,
            includeStart = includeEmail,
            fieldTimestamp = Constants.CREATE_AT
        )
    }

    suspend fun getNewEmails(
        includeEmail: Boolean,
        numberResult: Long,
        emailId: String?
    ): List<EmailData> {
        return collectionEmail.getNewObjects(
            endWithId = emailId,
            nResults = numberResult,
            includeEnd = includeEmail,
            transform = ::fromDocument,
            fieldTimestamp = Constants.CREATE_AT
        )
    }

    suspend fun deleterEmail(idEmail: String) {
        collectionEmail.document(idEmail).delete().await()
    }

    suspend fun updateEmail(updateEmailDTO: UpdateEmailDTO) {
        collectionEmail.document(updateEmailDTO.idEmail).update(updateEmailDTO.toUpdateMap())
            .await()
    }

    suspend fun deleterListEmails(listIds: List<String>) {
        val listOperations = listIds.map {
            collectionEmail.document(it).delete()
        }
        listOperations.awaitAll()
    }

    private fun fromDocument(document: DocumentSnapshot): EmailData? {
        val createAt = document.getTimeEstimate(Constants.CREATE_AT)
        val updateAt = document.getTimeEstimate(Constants.UPDATE_AT)
        return document.toObject<EmailData>()?.copy(
            idEmail = document.id,
            createdAt = createAt,
            updatedAt = updateAt
        )
    }
}