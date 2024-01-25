package com.nullpointer.nullsiteadmin.domain.auth

import com.nullpointer.nullsiteadmin.datasource.auth.local.AuthLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.auth.remote.AuthRemoteDataSource
import com.nullpointer.nullsiteadmin.datasource.infoPhone.local.InfoPhoneLocalDataSource
import com.nullpointer.nullsiteadmin.models.credentials.dto.CredentialsDTO
import com.nullpointer.nullsiteadmin.models.credentials.wrapper.CredentialsWrapper
import com.nullpointer.nullsiteadmin.models.phoneInfo.dto.UpdateInfoPhoneDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthRepoImpl(
    private val authLocalDataSource: AuthLocalDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val infoPhoneLocalDataSource: InfoPhoneLocalDataSource,
) : AuthRepository {

    override val isUserAuth: Flow<Boolean> = authLocalDataSource.getAuthData().map { it != null }

    override suspend fun logout(){
        authRemoteDataSource.logOut()
        authLocalDataSource.deleterAuthData()
    }

    override suspend fun verifyInfoPhoneData() {

        val authData = authLocalDataSource.getAuthData().first() ?: return

        val currentInfoPhone = infoPhoneLocalDataSource.getCurrentInfoPhone()
        val savedInfoPhone = infoPhoneLocalDataSource.getSavedInfoPhone()

        if (currentInfoPhone != savedInfoPhone) {
            authRemoteDataSource.updateInfoPhone(
                uuidPhone = currentInfoPhone.uuidPhone,
                updateInfoPhoneDTO = UpdateInfoPhoneDTO.fromInfoPhoneData(
                    idUser = authData.id,
                    infoPhoneData = currentInfoPhone
                )
            )
            infoPhoneLocalDataSource.updateSavedData(currentInfoPhone)
        }


    }

    override suspend fun login(credentialsWrapper: CredentialsWrapper) {
        val credentialsDTO = CredentialsDTO.fromCredentialsWrapper(
            credentialsWrapper = credentialsWrapper
        )
        val authData = authRemoteDataSource.login(credentialsDTO)
        authLocalDataSource.updateAuthData(authData)
    }
}