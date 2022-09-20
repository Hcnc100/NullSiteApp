package com.nullpointer.nullsiteadmin.domain.settings

import com.nullpointer.nullsiteadmin.models.UserAuth
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isAuthUser: Flow<Boolean>
    val user: Flow<UserAuth>
    suspend fun saveUser(user: UserAuth)
    suspend fun clearData()
}