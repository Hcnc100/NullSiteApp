package com.nullpointer.nullsiteadmin.data.remote.email

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.core.utils.awaitAll
import com.nullpointer.nullsiteadmin.core.utils.concatenateObjects
import com.nullpointer.nullsiteadmin.core.utils.getTimeEstimate
import com.nullpointer.nullsiteadmin.core.utils.newObjects
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class EmailRemoteDataSourceImpl : EmailRemoteDataSource {
    companion object {
        private const val EMAil_COLLECTION = "emails"
        private const val TIMESTAMP_FIELD = "timestamp"
        private const val IS_OPEN_FILED = "isOpen"
    }

    private val collectionEmail = Firebase.firestore.collection(EMAil_COLLECTION)


    override fun getAllEmails(): Flow<List<EmailContact>> = callbackFlow {
        val listener = collectionEmail.orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
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


    override suspend fun getConcatenateEmails(
        includeEmail: Boolean,
        emailId: String?,
        numberResult: Long
    ): List<EmailContact> {
        return collectionEmail.concatenateObjects(
            nResults = numberResult,
            startWithId = emailId,
            transform = ::fromDocument,
            includeStart = includeEmail,
            fieldTimestamp = TIMESTAMP_FIELD
        )
    }

    override suspend fun getNewEmails(
        includeEmail: Boolean,
        numberResult: Long,
        emailId: String?
    ): List<EmailContact> {
        return collectionEmail.newObjects(
            endWithId = emailId,
            nResults = numberResult,
            includeEnd = includeEmail,
            transform = ::fromDocument,
            fieldTimestamp = TIMESTAMP_FIELD
        )
    }

    override suspend fun deleterEmail(idEmail: String) {
        collectionEmail.document(idEmail).delete().await()
    }

    override suspend fun markAsOpen(idEmail: String) {
        collectionEmail.document(idEmail).update(mapOf(IS_OPEN_FILED to true)).await()
    }

    override suspend fun deleterListEmails(listIds: List<String>) {
        val listOperations = listIds.map {
            collectionEmail.document(it).delete()
        }
        listOperations.awaitAll()
    }

    private fun fromDocument(document: DocumentSnapshot): EmailContact? {
        return try {
            document.toObject<EmailContact>()?.copy(
                timestamp = document.getTimeEstimate(TIMESTAMP_FIELD),
                id = document.id
            )
        } catch (e: Exception) {
            Timber.e("Error cast ${document.id} to email")
            null
        }
    }
}