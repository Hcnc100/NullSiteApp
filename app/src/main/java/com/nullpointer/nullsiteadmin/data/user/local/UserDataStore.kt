package com.nullpointer.nullsiteadmin.data.user.local


import kotlinx.serialization.encodeToString


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class UserDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private const val KEY_USER_DATA = "KEY_USER_DATA"
    }

    private val keyUserData = stringPreferencesKey(KEY_USER_DATA)


    fun getPersonalInfo(): Flow<PersonalInfoData?> = dataStore.data.map { pref ->
        return@map pref[keyUserData]?.let {
            Json.decodeFromString(it)
        }
    }

    suspend fun updatePersonalInfo(
        newPersonalInfoData: PersonalInfoData
    ) {
        dataStore.edit { pref ->
           val userDataEncode= Json.encodeToString(newPersonalInfoData)
            pref[keyUserData]= userDataEncode
        }
    }

    suspend fun deleterPersonalInfo() {
        dataStore.edit { pref ->
            pref.clear()
        }
    }

}