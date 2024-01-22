package com.nullpointer.nullsiteadmin.domain.settings

import com.nullpointer.nullsiteadmin.models.settings.data.SettingsData
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettingsData(): Flow<SettingsData?>
    suspend fun saveSettingsData(settingsData: SettingsData)
}