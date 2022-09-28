package com.nullpointer.nullsiteadmin.data.local.settings

import com.nullpointer.nullsiteadmin.models.UserAuth
import kotlinx.coroutines.flow.Flow


interface SettingsDataSource {
    fun getUserAuth(): Flow<UserAuth>
    fun isAuthUser(): Flow<Boolean>
    fun isBiometricEnabled(): Flow<Boolean>
    fun timeOutLocked(): Flow<Long>
    suspend fun clearData()
    suspend fun saveUserAuth(userAuth: UserAuth)
    suspend fun updateTokenMsg(newToken: String)
    suspend fun changeTimeOutLocked(timeNowLocked: Long)
    suspend fun changeBiometricEnabled(newValue: Boolean)
}