package com.nullpointer.nullsiteadmin.data.local.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.nullpointer.nullsiteadmin.models.UserAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsDataSource {

    companion object {
        private const val KEY_ID_USER = "KEY_ID_USER"
        private const val KEY_EMAIL_USER = "KEY_EMAIL_USER"
        private const val KEY_TOKEN_MSG_USER = "KEY_TOKEN_MSG_USER"
        private const val KEY_IS_BIOMETRIC_ENABLE = "KEY_IS_BIOMETRIC_ENABLE"
        private const val KEY_TIME_OUT_LOCKED = "KEY_TIME_OUT_LOCKED"
    }

    private val keyIdUser = stringPreferencesKey(KEY_ID_USER)
    private val keyEmailUser = stringPreferencesKey(KEY_EMAIL_USER)
    private val keyTokenMsgUser = stringPreferencesKey(KEY_TOKEN_MSG_USER)
    private val keyIsBiometricEnable = booleanPreferencesKey(KEY_IS_BIOMETRIC_ENABLE)
    private val keyTimeOutLocked = longPreferencesKey(KEY_TIME_OUT_LOCKED)

    override fun isAuthUser(): Flow<Boolean> = getUserAuth().map { it.id.isNotEmpty() }

    override fun isBiometricEnabled(): Flow<Boolean> = dataStore.data.map { pref ->
        pref[keyIsBiometricEnable] ?: false
    }

    override fun timeOutLocked(): Flow<Long> = dataStore.data.map { pref ->
        pref[keyTimeOutLocked] ?: 0L
    }

    override suspend fun changeTimeOutLocked(timeNowLocked: Long) {
        dataStore.edit { pref ->
            pref[keyTimeOutLocked] = timeNowLocked
        }
    }

    override suspend fun changeBiometricEnabled(newValue: Boolean) {
        dataStore.edit { pref ->
            pref[keyIsBiometricEnable] = newValue
        }
    }

    override fun getUserAuth() = dataStore.data.map { pref ->
        UserAuth(
            id = pref[keyIdUser] ?: "",
            email = pref[keyEmailUser] ?: "",
            tokenMsg = pref[keyTokenMsgUser] ?: ""
        )
    }

    override suspend fun saveUserAuth(userAuth: UserAuth) {
        dataStore.edit { pref ->
            pref[keyIdUser] = userAuth.id
            pref[keyEmailUser] = userAuth.email
            pref[keyTokenMsgUser] = userAuth.tokenMsg
        }
    }

    override suspend fun updateTokenMsg(newToken: String) {
        dataStore.edit { pref ->
            pref[keyTokenMsgUser] = newToken
        }
    }

    override suspend fun clearData() {
        dataStore.edit { pref ->
            pref.clear()
        }
    }
}