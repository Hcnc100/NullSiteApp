package com.nullpointer.nullsiteadmin.domain.settings

import com.nullpointer.nullsiteadmin.data.local.SettingsDataSource
import com.nullpointer.nullsiteadmin.models.UserAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepoImpl(
    private val settingsDataSource: SettingsDataSource
) : SettingsRepository {

    override val user: Flow<UserAuth> = settingsDataSource.getUserAuth()

    override val isAuthUser: Flow<Boolean> = user.map { it.id.isNotEmpty() }

    override suspend fun saveUser(user: UserAuth) =
        settingsDataSource.saveUserAuth(user)

    override suspend fun clearData() = settingsDataSource.clearData()
}