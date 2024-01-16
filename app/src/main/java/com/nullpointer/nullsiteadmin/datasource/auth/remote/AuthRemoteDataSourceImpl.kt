package com.nullpointer.nullsiteadmin.datasource.auth.remote

import com.nullpointer.nullsiteadmin.core.utils.callApiTimeOut
import com.nullpointer.nullsiteadmin.data.auth.remote.AuthApiServices
import com.nullpointer.nullsiteadmin.models.data.AuthData
import com.nullpointer.nullsiteadmin.models.dto.CredentialsDTO
import com.nullpointer.nullsiteadmin.models.dto.UpdateInfoPhoneDTO

class AuthRemoteDataSourceImpl(
    private val authApiServices: AuthApiServices
):AuthRemoteDataSource {
    override suspend fun login(credentialsDTO: CredentialsDTO):AuthData {
        val response = callApiTimeOut {
            authApiServices.login(credentialsDTO)
        }
        return AuthData.fromAuthResponse(response)
    }

    override suspend fun updateInfoPhone(
        uuidPhone:String,
        updateInfoPhoneDTO: UpdateInfoPhoneDTO
    ) {
       callApiTimeOut {
            authApiServices.updateInfoPhone(
                uuidPhone = uuidPhone,
                updateInfoPhoneDTO = updateInfoPhoneDTO
            )
       }
    }

    override suspend fun logOut() = authApiServices.logout()
}