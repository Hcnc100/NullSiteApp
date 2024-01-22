package com.nullpointer.nullsiteadmin.data.settings.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nullpointer.nullsiteadmin.models.settings.data.SettingsData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private const val KEY_SETTINGS_USER = "KEY_SETTINGS_USER"
    }

    private val keySettingsUser = stringPreferencesKey(KEY_SETTINGS_USER)
    fun getSettingsData(): Flow<SettingsData?> = dataStore.data.map { pref ->
        pref[keySettingsUser]?.let { json ->
            Json.decodeFromString(json)
        }
    }

    suspend fun saveSettingsData(settingsData: SettingsData) {
        dataStore.edit { pref ->
            pref[keySettingsUser] = Json.encodeToString(settingsData)
        }
    }


    suspend fun clearData() {
        dataStore.edit { pref ->
            pref.remove(keySettingsUser)
        }
    }
}