package com.nullpointer.nullsiteadmin.datasource.user.remote

import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.dto.PersonalInfoDTO

interface InfoUserRemoteDataSource {
    suspend fun updatePersonalInfo(
        idUser:String,
        personalInfoDTO: PersonalInfoDTO
    )
    suspend fun getPersonalInfo(
        idUser: String
    ): PersonalInfoData?

}