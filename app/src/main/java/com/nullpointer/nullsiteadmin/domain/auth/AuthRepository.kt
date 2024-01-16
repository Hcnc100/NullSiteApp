package com.nullpointer.nullsiteadmin.domain.auth

import com.nullpointer.nullsiteadmin.models.wrapper.CredentialsWrapper
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isUserAuth: Flow<Boolean>
    suspend fun logout()
    suspend fun verifyTokenMessaging()

    suspend fun login(credentialsWrapper: CredentialsWrapper)
}