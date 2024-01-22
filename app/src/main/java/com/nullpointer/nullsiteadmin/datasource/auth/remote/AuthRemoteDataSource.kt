package com.nullpointer.nullsiteadmin.datasource.auth.remote

import com.nullpointer.nullsiteadmin.models.auth.data.AuthData
import com.nullpointer.nullsiteadmin.models.credentials.dto.CredentialsDTO
import com.nullpointer.nullsiteadmin.models.phoneInfo.dto.UpdateInfoPhoneDTO

interface AuthRemoteDataSource {


    suspend fun login(credentialsDTO: CredentialsDTO): AuthData

     suspend fun updateInfoPhone(
         uuidPhone: String,
         updateInfoPhoneDTO: UpdateInfoPhoneDTO,
     )

    suspend fun logOut()
}