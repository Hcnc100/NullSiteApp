package com.nullpointer.nullsiteadmin.models.personalInfo.data

import com.nullpointer.nullsiteadmin.models.personalInfo.response.PersonalInfoResponse
import kotlinx.serialization.Serializable

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