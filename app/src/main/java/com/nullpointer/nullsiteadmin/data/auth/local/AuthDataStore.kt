package com.nullpointer.nullsiteadmin.data.auth.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nullpointer.nullsiteadmin.models.auth.data.AuthData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AuthDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object{
        const val KEY_AUTH_DATA="KEY_AUTH_DATA"
    }

    private val keyAuthData= stringPreferencesKey(KEY_AUTH_DATA)

    fun getAuthData(): Flow<AuthData?> = dataStore.data.map { pref->
        pref[keyAuthData]?.let {
            Json.decodeFromString(it)
        }
    }

    suspend fun updateAuthData(authData: AuthData) {
        dataStore.edit { pref->
            val authDataEncode= Json.encodeToString(authData)
            pref[keyAuthData]= authDataEncode
        }
    }

    suspend fun deleterAuthData() {
        dataStore.edit { pref->
            pref.remove(keyAuthData)
        }
    }
}