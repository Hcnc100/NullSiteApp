package com.nullpointer.nullsiteadmin.datasource.user.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.core.utils.Constants
import com.nullpointer.nullsiteadmin.core.utils.getTimeEstimate
import com.nullpointer.nullsiteadmin.models.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.dto.PersonalInfoDTO
import com.nullpointer.nullsiteadmin.models.response.PersonalInfoResponse
import kotlinx.coroutines.tasks.await
import java.util.*

class InfoUserRemoteDataSourceImpl : InfoUserRemoteDataSource {

    private val refMyInfo = Firebase.firestore.collection(Constants.INFO_USER_COLLECTIONS)


    override suspend fun updatePersonalInfo(
        id: String,
        personalInfoDTO: PersonalInfoDTO,
    ) {
        refMyInfo.document(id).update(personalInfoDTO.toUpdateMap()).await()
    }


    override suspend fun getPersonalInfo(
        id: String,
    ): PersonalInfoData? {
        val refUser = refMyInfo.document(id).get().await()
        val personalInfoResponse= fromDocument(refUser)

        return personalInfoResponse?.let {
            PersonalInfoData.fromPersonalInfoResponse(it)
        }
    }


    private fun fromDocument(document: DocumentSnapshot): PersonalInfoResponse? {
        return document.toObject<PersonalInfoResponse>()?.copy(
            lastUpdate = document.getTimeEstimate(Constants.nameFieldUpdate) ?: Date(),
            id = document.id
        )
    }

}
