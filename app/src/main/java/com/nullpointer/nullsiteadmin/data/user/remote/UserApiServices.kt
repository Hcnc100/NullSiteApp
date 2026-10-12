package com.nullpointer.nullsiteadmin.data.user.remote

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.nullpointer.nullsiteadmin.BuildConfig
import com.nullpointer.nullsiteadmin.core.utils.Constants
import com.nullpointer.nullsiteadmin.core.utils.ContentEditGuard
import com.nullpointer.nullsiteadmin.core.utils.getTimeEstimate
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileContentContract
import com.nullpointer.nullsiteadmin.models.personalInfo.dto.PersonalInfoDTO
import com.nullpointer.nullsiteadmin.models.personalInfo.response.PersonalInfoResponse
import kotlinx.coroutines.tasks.await

class UserApiServices {

    private val refMyInfo = Firebase.firestore.collection(Constants.USER_COLLECTION)


     suspend fun updatePersonalInfo(
         idUser: String,
         personalInfoDTO: PersonalInfoDTO,
         baseline: PersonalInfoData?,
    ) {
         val refUser = refMyInfo.document(BuildConfig.ID_INFO_PROFILE_FIREBASE)

         val patch = personalInfoDTO.toUpdateMap()
         val create = personalInfoDTO.toCreateMap()
         Firebase.firestore.runTransaction { transaction ->
             val current = transaction.get(refUser)
             ContentEditGuard.requireUnchanged(current.data, ContentEditGuard.profileBaseline(baseline))
             if (current.exists()) transaction.update(refUser, patch)
             else transaction.set(refUser, create)
         }.await()
    }


     suspend fun getPersonalInfo(
        idUser: String,
    ): PersonalInfoData? {
        val refUser = refMyInfo.document(BuildConfig.ID_INFO_PROFILE_FIREBASE).get().await()
        val personalInfoResponse = fromDocument(refUser)

        return personalInfoResponse?.let {
            PersonalInfoData.fromPersonalInfoResponse(it).copy(
                socialLinks = ProfileContentContract.readLinks(refUser.get("socialLinks")),
                translations = ProfileContentContract.readTranslations(refUser.get("translations")),
            )
        }
    }


    private fun fromDocument(document: DocumentSnapshot): PersonalInfoResponse? {
        return document.toObject<PersonalInfoResponse>()?.copy(
            id = document.id,
            createAt = document.getTimeEstimate(Constants.UPDATED_AT),
            updateAt = document.getTimeEstimate(Constants.UPDATED_AT)
        )
    }
}
