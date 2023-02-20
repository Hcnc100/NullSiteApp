package com.nullpointer.nullsiteadmin.domain.infoUser

import android.net.Uri
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import kotlinx.coroutines.flow.Flow

interface InfoUserRepository {

    val myPersonalInfo: Flow<PersonalInfo>
    suspend fun updatePersonalInfo(personalInfo: PersonalInfo)

    suspend fun updatePersonalInfo(personalInfo: PersonalInfo, uriImage: Uri?)
    suspend fun requestLastPersonalInfo(forceRefresh: Boolean): Boolean
}