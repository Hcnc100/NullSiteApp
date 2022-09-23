package com.nullpointer.nullsiteadmin.data.local.infoUser

import com.nullpointer.nullsiteadmin.models.PersonalInfo
import kotlinx.coroutines.flow.Flow

interface InfoUserLocalDataSource {
    fun getPersonalInfo(): Flow<PersonalInfo>
    suspend fun updatePersonalInfo(newPersonalInfo: PersonalInfo)
    suspend fun deleterPersonalInfo()
}