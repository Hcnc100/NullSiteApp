package com.nullpointer.nullsiteadmin.datasource.settings.local

import com.nullpointer.nullsiteadmin.data.settings.local.SettingsDataStore
import com.nullpointer.nullsiteadmin.models.settings.data.SettingsData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SettingsLocalDataSourceImpl(
    private val settingsDataStore: SettingsDataStore
) : SettingsLocalDataSource {


    override fun getSettingsData(): Flow<SettingsData?> =
        settingsDataStore.getSettingsData()

    override suspend fun saveSettingsData(settingsData: SettingsData) =
        settingsDataStore.saveSettingsData(settingsData)


    override suspend fun clearData() =
        settingsDataStore.clearData()

    override suspend fun changeBiometricEnabled(isEnable: Boolean) {
        val currentData= settingsDataStore.getSettingsData().first() ?: SettingsData()
        val newData = currentData.copy(isBiometricEnabled = isEnable)
        settingsDataStore.saveSettingsData(newData)
    }

    override suspend fun changeTimeOutLocked(timeOut: Long) {
        val currentData= settingsDataStore.getSettingsData().first() ?: SettingsData()
        val newData = currentData.copy(timeOutLock = timeOut)
        settingsDataStore.saveSettingsData(newData)
    }
}