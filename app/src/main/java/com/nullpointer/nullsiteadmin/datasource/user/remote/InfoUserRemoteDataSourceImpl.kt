package com.nullpointer.nullsiteadmin.datasource.user.remote

import com.nullpointer.nullsiteadmin.data.user.remote.UserApiServices
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.dto.PersonalInfoDTO

class InfoUserRemoteDataSourceImpl(
    private val userApiServices: UserApiServices
) : InfoUserRemoteDataSource {
    override suspend fun updatePersonalInfo(
        idUser: String,
        personalInfoDTO: PersonalInfoDTO
    ) =
        userApiServices.updatePersonalInfo(
            idUser = idUser,
            personalInfoDTO = personalInfoDTO
        )

    override suspend fun getPersonalInfo(
        idUser: String
    ): PersonalInfoData? =
        userApiServices.getPersonalInfo(
            idUser = idUser
        )

}
