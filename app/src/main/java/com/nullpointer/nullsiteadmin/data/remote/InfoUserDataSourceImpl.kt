package com.nullpointer.nullsiteadmin.data.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class InfoUserDataSourceImpl : InfoUserDataSource {
    companion object {
        const val INFO_COLLECTIONS = "infoProfile"
        const val MY_ID_DOC = "nullPointer"
    }

    private val refMyInfo = Firebase.firestore.collection(INFO_COLLECTIONS).document(MY_ID_DOC)

    override fun getMyInfo(): Flow<PersonalInfo> = callbackFlow {
        val listener = refMyInfo.addSnapshotListener { value, error ->
            error?.let { channel.close(it) }
            try {
                val info = value!!.toObject(PersonalInfo::class.java)!!
                trySend(info)
            } catch (e: Exception) {
                channel.close(CancellationException(e))
            }
        }

        awaitClose {
            Timber.d("Close flow info profile")
            listener.remove()
        }
    }

    override suspend fun updateAnyInfo(
        nameAdmin: String?,
        profession: String?,
        description: String?
    ) {
        nameAdmin?.let { refMyInfo.update(mapOf("name" to it)).await() }
        profession?.let { refMyInfo.update(mapOf("profession" to it)).await() }
        description?.let { refMyInfo.update(mapOf("description" to it)).await() }
    }
}