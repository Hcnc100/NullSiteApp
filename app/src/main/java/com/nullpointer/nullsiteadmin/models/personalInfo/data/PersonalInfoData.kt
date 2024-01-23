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

        val example = PersonalInfoData(
            name = "Null Pointer",
            urlImg = "https://avatars.githubusercontent.com/u/48194378?v=4",
            updateAt = 1627776000000,
            profession = "Android Developer",
            description = "I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have"
        )

        val descriptionLong = PersonalInfoData(
            name = "Null Pointer",
            urlImg = "https://avatars.githubusercontent.com/u/48194378?v=4",
            updateAt = 1627776000000,
            profession = "Android Developer",
            description = "I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have I am a software developer with 2 years of experience in the development of mobile applications for the Android platform, I have"
        )

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