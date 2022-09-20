package com.nullpointer.nullsiteadmin.data.local

import com.nullpointer.nullsiteadmin.models.UserAuth
import kotlinx.coroutines.flow.Flow


interface SettingsDataSource {
    fun getUserAuth(): Flow<UserAuth>
    fun isAuthUser(): Flow<Boolean>
    suspend fun clearData()
    suspend fun saveUserAuth(userAuth: UserAuth)
    suspend fun updateTokenMsg(newToken: String)
}