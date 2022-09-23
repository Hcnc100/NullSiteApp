package com.nullpointer.nullsiteadmin.data.remote.infoUser

import com.nullpointer.nullsiteadmin.models.PersonalInfo
import java.util.*

interface InfoUserRemoteDataSource {
    //    fun getMyInfo(): Flow<PersonalInfo>
    suspend fun updatePersonalInfo(personalInfo: PersonalInfo): PersonalInfo?
    suspend fun getMoreRecentPersonalInfo(timestamp: Date?): PersonalInfo?
}