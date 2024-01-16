package com.nullpointer.nullsiteadmin.datasource.auth.remote

import com.nullpointer.nullsiteadmin.models.data.AuthData
import com.nullpointer.nullsiteadmin.models.data.InfoPhoneData
import com.nullpointer.nullsiteadmin.models.dto.CredentialsDTO
import com.nullpointer.nullsiteadmin.models.dto.UpdateInfoPhoneDTO

interface AuthRemoteDataSource {


    suspend fun login(credentialsDTO: CredentialsDTO):AuthData

     suspend fun updateInfoPhone(
         uuidPhone: String,
         updateInfoPhoneDTO: UpdateInfoPhoneDTO,
     )

    suspend fun logOut()
}