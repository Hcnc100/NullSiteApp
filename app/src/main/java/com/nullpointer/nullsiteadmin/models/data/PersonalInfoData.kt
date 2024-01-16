package com.nullpointer.nullsiteadmin.models.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.nullpointer.nullsiteadmin.models.response.PersonalInfoResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.Date

@Serializable
data class PersonalInfoData(
    val id: String ="",
    val name: String ="",
    val urlImg: String ="",
    val lastUpdate: Long =0,
    val profession: String="",
    val description: String="",
){
    companion object{
        fun fromPersonalInfoResponse(
            personalInfoResponse: PersonalInfoResponse
        ): PersonalInfoData {

            return PersonalInfoData(
                id = personalInfoResponse.id,
                name = personalInfoResponse.name,
                description = personalInfoResponse.description,
                lastUpdate = personalInfoResponse.lastUpdate.time,
                profession = personalInfoResponse.profession,
                urlImg = personalInfoResponse.urlImg
            )
        }
    }
}