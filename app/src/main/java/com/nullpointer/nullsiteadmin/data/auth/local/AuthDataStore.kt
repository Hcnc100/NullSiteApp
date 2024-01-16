package com.nullpointer.nullsiteadmin.data.auth.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nullpointer.nullsiteadmin.models.data.AuthData
import kotlinx.coroutines.flow.Flow

class AuthDataStore(
    private val dataStore: DataStore<Preferences>
) {

    fun getAuthData(): Flow<AuthData?> {
        TODO()
    }

    suspend fun updateAuthData(authData: AuthData) {}

    suspend fun deleterAuthData() {}
}