package com.nullpointer.nullsiteadmin.datasource.user.local

import com.nullpointer.nullsiteadmin.models.data.PersonalInfoData
import kotlinx.coroutines.flow.Flow

interface InfoUserLocalDataSource {
    fun getPersonalInfo(): Flow<PersonalInfoData?>
    suspend fun updatePersonalInfo(newPersonalInfoData: PersonalInfoData)

    suspend fun deleterPersonalInfo()
}