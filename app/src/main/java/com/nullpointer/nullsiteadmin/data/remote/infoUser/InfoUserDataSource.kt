package com.nullpointer.nullsiteadmin.data.remote.infoUser

import com.nullpointer.nullsiteadmin.models.PersonalInfo
import kotlinx.coroutines.flow.Flow

interface InfoUserDataSource {
    fun getMyInfo(): Flow<PersonalInfo>
    suspend fun updateAnyInfo(nameAdmin: String?, profession: String?, description: String?)
}