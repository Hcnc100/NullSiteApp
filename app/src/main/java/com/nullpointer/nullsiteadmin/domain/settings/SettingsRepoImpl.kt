package com.nullpointer.nullsiteadmin.domain.settings

import com.nullpointer.nullsiteadmin.datasource.settings.local.SettingsLocalDataSource
import com.nullpointer.nullsiteadmin.models.settings.data.SettingsData
import kotlinx.coroutines.flow.Flow

class SettingsRepoImpl(
    private val settingsLocalDataSource: SettingsLocalDataSource
) : SettingsRepository {
    override fun getSettingsData(): Flow<SettingsData?> =
        settingsLocalDataSource.getSettingsData()

    override suspend fun saveSettingsData(settingsData: SettingsData) =
        settingsLocalDataSource.saveSettingsData(settingsData)


}