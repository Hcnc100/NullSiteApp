package com.nullpointer.nullsiteadmin.data.remote.infoUser

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.core.utils.getNewObject
import com.nullpointer.nullsiteadmin.core.utils.getTimeEstimate
import com.nullpointer.nullsiteadmin.core.utils.serializeToMap
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import kotlinx.coroutines.tasks.await
import java.util.*

class InfoUserRemoteDataSourceImpl : InfoUserRemoteDataSource {
    companion object {
        private const val INFO_COLLECTIONS = "infoProfile"
        private const val FIELD_LAST_UPDATE = "lastUpdate"
    }

    private val refMyInfo = Firebase.firestore.collection(INFO_COLLECTIONS)


    override suspend fun updatePersonalInfo(personalInfo: PersonalInfo): PersonalInfo? {
        val mapPersonalInfo = personalInfo.serializeToMap(FIELD_LAST_UPDATE)
        val refInfoPerson = refMyInfo.document(personalInfo.id)
        refMyInfo.document(personalInfo.id).update(mapPersonalInfo).await()
        return fromDocument(refInfoPerson.get().await())
    }


    override suspend fun getMoreRecentPersonalInfo(
        timestamp: Date?
    ): PersonalInfo? {
        return refMyInfo.getNewObject(
            timestamp = timestamp,
            fieldTimestamp = FIELD_LAST_UPDATE,
            transform = ::fromDocument
        )
    }


    private fun fromDocument(document: DocumentSnapshot): PersonalInfo? {
        return document.toObject<PersonalInfo>()?.copy(
            lastUpdate = document.getTimeEstimate(FIELD_LAST_UPDATE),
            id = document.id
        )
    }

}
