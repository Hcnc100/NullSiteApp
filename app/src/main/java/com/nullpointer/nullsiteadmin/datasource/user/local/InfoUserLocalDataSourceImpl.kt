package com.nullpointer.nullsiteadmin.datasource.user.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nullpointer.nullsiteadmin.data.user.local.UserDataStore
import com.nullpointer.nullsiteadmin.models.data.PersonalInfoData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

class InfoUserLocalDataSourceImpl(
    private val userDataStore: UserDataStore
) : InfoUserLocalDataSource {

    companion object {
        private const val KEY_USER_DATA = "KEY_USER_DATA"
    }

    private val keyUserData = stringPreferencesKey(KEY_USER_DATA)


    override fun getPersonalInfo() = userDataStore.getPersonalInfo()

    override suspend fun updatePersonalInfo(
        newPersonalInfoData: PersonalInfoData
    ) = userDataStore.updatePersonalInfo(newPersonalInfoData)



    override suspend fun deleterPersonalInfo()= userDataStore.deleterPersonalInfo()

}