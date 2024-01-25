package com.nullpointer.nullsiteadmin.data.email.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.core.utils.Constants
import com.nullpointer.nullsiteadmin.core.utils.awaitAll
import com.nullpointer.nullsiteadmin.core.utils.getTimeEstimate
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import com.nullpointer.nullsiteadmin.models.email.dto.UpdateEmailDTO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class EmailApiServices {


    private val collectionEmail = Firebase.firestore.collection(Constants.EMAIL_COLLECTION)


    fun getAllEmails(): Flow<List<EmailData>> = callbackFlow {
        val listener = collectionEmail.orderBy(Constants.CREATED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let { channel.close(it) }
                try {
                    val listEmails = value!!.documents.mapNotNull(::fromDocument)
                    trySend(listEmails)
                } catch (e: Exception) {
                    Timber.e("Error when transform document in email list $e")
                    channel.close(e)
                }
            }
        awaitClose { listener.remove() }
    }


    suspend fun getConcatenateEmails(
        emailId: String,
        numberResult: Long
    ): List<EmailData> {
        val lastEmailDoc = collectionEmail.document(emailId).get().await()
        val response = collectionEmail.orderBy(Constants.CREATED_AT, Query.Direction.DESCENDING)
            .startAfter(lastEmailDoc).limit(numberResult).get().await()
        return response.documents.mapNotNull(::fromDocument)
    }

    suspend fun getNewEmails(
        numberResult: Long,
    ): List<EmailData> {
        val response = collectionEmail.orderBy(Constants.CREATED_AT, Query.Direction.DESCENDING)
            .limit(numberResult).get().await()
        return response.documents.mapNotNull(::fromDocument)
    }

    suspend fun deleterEmail(idEmail: String) {
        collectionEmail.document(idEmail).delete().await()
    }

    suspend fun updateEmail(
        idEmail: String,
        updateEmailDTO: UpdateEmailDTO,
    ) {
        collectionEmail.document(idEmail).update(updateEmailDTO.toUpdateMap())
            .await()
    }

    suspend fun deleterListEmails(listIds: List<String>) {
        val listOperations = listIds.map {
            collectionEmail.document(it).delete()
        }
        listOperations.awaitAll()
    }

    private fun fromDocument(document: DocumentSnapshot): EmailData? {
        val createAt = document.getTimeEstimate(Constants.CREATED_AT)
        val updateAt = document.getTimeEstimate(Constants.UPDATED_AT)
        return document.toObject<EmailData>()?.copy(
            idEmail = document.id,
            createdAt = createAt,
            updatedAt = updateAt
        )
    }
}