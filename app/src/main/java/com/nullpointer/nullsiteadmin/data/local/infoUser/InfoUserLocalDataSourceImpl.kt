package com.nullpointer.nullsiteadmin.data.local.infoUser

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class InfoUserLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : InfoUserLocalDataSource {

    companion object {
        private const val KEY_NAME_USER = "KEY_NAME_USER"
        private const val KEY_DESCRIPTION_USER = "KEY_DESCRIPTION_USER"
        private const val KEY_PROFESSION_USER = "KEY_PROFESSION_USER"
        private const val KEY_URL_PROFILE_USER = "KEY_URL_PROFILE_USER"
        private const val KEY_LAST_UPDATE_USER = "KEY_LAST_UPDATE_USER"
        private const val KEY_ID_USER = "KEY_ID_USER"
    }

    private val idUser = stringPreferencesKey(KEY_ID_USER)
    private val keyNameUser = stringPreferencesKey(KEY_NAME_USER)
    private val professionUser = stringPreferencesKey(KEY_PROFESSION_USER)
    private val urlProfileUser = stringPreferencesKey(KEY_URL_PROFILE_USER)
    private val descriptionUser = stringPreferencesKey(KEY_DESCRIPTION_USER)
    private val timeLastUpdatedUser = longPreferencesKey(KEY_LAST_UPDATE_USER)


    override fun getPersonalInfo(): Flow<PersonalInfo> = dataStore.data.map { pref ->
        PersonalInfo(
            id = pref[idUser] ?: "",
            name = pref[keyNameUser] ?: "",
            urlImg = pref[urlProfileUser] ?: "",
            profession = pref[professionUser] ?: "",
            description = pref[descriptionUser] ?: "",
            lastUpdate = pref[timeLastUpdatedUser]?.let { Date(it) }
        )
    }

    override suspend fun updatePersonalInfo(
        newPersonalInfo: PersonalInfo
    ) {
        dataStore.edit { pref ->
            pref[idUser] = newPersonalInfo.id
            pref[keyNameUser] = newPersonalInfo.name
            pref[urlProfileUser] = newPersonalInfo.urlImg
            pref[professionUser] = newPersonalInfo.profession
            pref[descriptionUser] = newPersonalInfo.description
            newPersonalInfo.lastUpdate?.let { pref[timeLastUpdatedUser] = it.time }
        }
    }

    override suspend fun deleterPersonalInfo() {
        dataStore.edit { pref ->
            pref.clear()
        }
    }

}