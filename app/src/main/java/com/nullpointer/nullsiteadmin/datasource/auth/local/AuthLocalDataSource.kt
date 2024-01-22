package com.nullpointer.nullsiteadmin.datasource.auth.local

import com.nullpointer.nullsiteadmin.models.auth.data.AuthData
import kotlinx.coroutines.flow.Flow

interface AuthLocalDataSource {

    fun getAuthData():Flow<AuthData?>

    suspend fun updateAuthData(authData: AuthData)

    suspend fun deleterAuthData()
}