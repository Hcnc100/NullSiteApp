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

class EmailDataSourceImpl : EmailDataSource {
    companion object {
        private const val EMAil_COLLECTION = "emails"
    }

    private val collectionEmail = Firebase.firestore.collection(EMAil_COLLECTION)


    override fun getAllEmails(): Flow<List<EmailContact>> = callbackFlow {
        val listener = collectionEmail.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let { channel.close(it) }
                try {
                    val listEmails = value!!.documents.mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject<EmailContact>()?.copy(
                            timestamp = documentSnapshot.getTimestamp(
                                "timestamp",
                                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                            )?.toDate(),
                            id = documentSnapshot.id
                        )
                    }
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
}