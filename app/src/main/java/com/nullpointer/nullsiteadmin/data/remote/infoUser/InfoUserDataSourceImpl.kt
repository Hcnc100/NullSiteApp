package com.nullpointer.nullsiteadmin.data.remote.infoUser

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
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
                val info = value?.toObject<PersonalInfo>()!!
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

    override suspend fun updatePersonalInfo(personalInfo: PersonalInfo) {
        refMyInfo.update(personalInfo.toMap()).await()
    }
}

private fun PersonalInfo.toMap(): Map<String, Any> {
    return mapOf(
        "name" to this.name,
        "description" to this.description,
        "profession" to this.profession,
        "urlImg" to this.urlImg
    )
}
