package com.nullpointer.nullsiteadmin.datasource.user.remote

import com.nullpointer.nullsiteadmin.models.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.dto.PersonalInfoDTO
import java.util.*

interface InfoUserRemoteDataSource {
    suspend fun updatePersonalInfo(
        idUser:String,
        personalInfoDTO: PersonalInfoDTO
    )
    suspend fun getPersonalInfo(
        idUser: String
    ):PersonalInfoData?

}