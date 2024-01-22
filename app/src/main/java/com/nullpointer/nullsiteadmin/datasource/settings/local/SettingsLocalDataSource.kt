package com.nullpointer.nullsiteadmin.datasource.settings.local

import com.nullpointer.nullsiteadmin.models.settings.data.SettingsData
import kotlinx.coroutines.flow.Flow


interface SettingsLocalDataSource {
    fun getSettingsData(): Flow<SettingsData?>
    suspend fun saveSettingsData(settingsData: SettingsData)
    suspend fun clearData()
   suspend fun changeBiometricEnabled(isEnable: Boolean)
   suspend fun changeTimeOutLocked(timeOut: Long)
}