package com.nullpointer.nullsiteadmin.data.infoPhone.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nullpointer.nullsiteadmin.models.phoneInfo.data.InfoPhoneData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class InfoPhoneDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object{
        private const val KEY_INFO_PHONE="KEY_INFO_PHONE"
    }

    private val keyInfoPhone= stringPreferencesKey(KEY_INFO_PHONE)

    suspend fun updateInfoPhone(infoPhoneData: InfoPhoneData){
        dataStore.edit { pref->
            val infoPhoneEncode= Json.encodeToString(infoPhoneData)
            pref[keyInfoPhone]= infoPhoneEncode

        }
    }

    fun getCurrentInfoPhoneSaved():Flow<InfoPhoneData?> = dataStore.data.map { pref->
        val infoPhoneEncode= pref[keyInfoPhone]
        return@map infoPhoneEncode?.let {
            Json.decodeFromString(it)
        }
    }

    suspend fun deleterData() {
        dataStore.edit {pref->
            pref[keyInfoPhone]= ""
        }
    }
}