package com.nullpointer.nullsiteadmin.data.user.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.BuildConfig
import com.nullpointer.nullsiteadmin.core.utils.Constants
import com.nullpointer.nullsiteadmin.core.utils.getTimeEstimate
import com.nullpointer.nullsiteadmin.models.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.dto.PersonalInfoDTO
import com.nullpointer.nullsiteadmin.models.response.PersonalInfoResponse
import kotlinx.coroutines.tasks.await
import java.util.Date

class UserApiServices {

    private val refMyInfo = Firebase.firestore.collection(Constants.USER_COLLECTION)


     suspend fun updatePersonalInfo(
        idUser: String,
        personalInfoDTO: PersonalInfoDTO,
    ) {
         val refUser = refMyInfo.document(BuildConfig.ID_INFO_PROFILE_FIREBASE)

        when (refUser.get().await().exists()) {
            true -> refUser.update(personalInfoDTO.toUpdateMap()).await()
            false -> refUser.set(personalInfoDTO.toCreateMap()).await()
        }
    }


     suspend fun getPersonalInfo(
        idUser: String,
    ): PersonalInfoData? {
        val refUser = refMyInfo.document(BuildConfig.ID_INFO_PROFILE_FIREBASE).get().await()
        val personalInfoResponse = fromDocument(refUser)

        return personalInfoResponse?.let {
            PersonalInfoData.fromPersonalInfoResponse(it)
        }
    }


    private fun fromDocument(document: DocumentSnapshot): PersonalInfoResponse? {
        return document.toObject<PersonalInfoResponse>()?.copy(
            id = document.id,
            createAt = document.getTimeEstimate(Constants.UPDATE_AT),
            updateAt = document.getTimeEstimate(Constants.UPDATE_AT)
        )
    }
}
