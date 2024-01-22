package com.nullpointer.nullsiteadmin.datasource.auth.local

import com.nullpointer.nullsiteadmin.data.auth.local.AuthDataStore
import com.nullpointer.nullsiteadmin.models.auth.data.AuthData
import kotlinx.coroutines.flow.Flow

class AuthLocalDatSourceImpl(
    private val authDataStore: AuthDataStore
):AuthLocalDataSource {
    override fun getAuthData(): Flow<AuthData?> = authDataStore.getAuthData()

    override suspend fun updateAuthData(authData: AuthData) = authDataStore.updateAuthData(authData)

    override suspend fun deleterAuthData() = authDataStore.deleterAuthData()
}