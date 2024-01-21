package com.nullpointer.nullsiteadmin.models.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.nullpointer.nullsiteadmin.models.response.PersonalInfoResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.Date

@Serializable
data class PersonalInfoData(
    val name: String,
    val urlImg: String,
    val updateAt: Long,
    val profession: String,
    val description: String,
){
    companion object{
        fun fromPersonalInfoResponse(
            personalInfoResponse: PersonalInfoResponse
        ): PersonalInfoData {

            return PersonalInfoData(
                name = personalInfoResponse.name,
                urlImg = personalInfoResponse.urlImg,
                profession = personalInfoResponse.profession,
                updateAt = personalInfoResponse.updateAt!!.time,
                description = personalInfoResponse.description,
            )
        }
    }
}