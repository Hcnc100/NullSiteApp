package com.nullpointer.nullsiteadmin.data.remote.email

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.models.EmailContact
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class EmailDataSourceImpl : EmailDataSource {
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

    override suspend fun deleterEmail(idEmail: String) {
        collectionEmail.document(idEmail).delete().await()
    }

    override suspend fun markAsOpen(idEmail: String) {
        collectionEmail.document(idEmail).update(mapOf(IS_OPEN_FILED to true)).await()
    }

    private fun fromDocument(document: DocumentSnapshot): EmailContact? {
        return try {
            document.toObject<EmailContact>()?.copy(
                timestamp = document.getTimestamp(
                    TIMESTAMP_FIELD,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )?.toDate(),
                id = document.id
            )
        } catch (e: Exception) {
            Timber.e("Error cast ${document.id} to email")
            null
        }
    }
}