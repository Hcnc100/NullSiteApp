package com.nullpointer.nullsiteadmin.domain.infoUser

import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.wrapper.UpdateInfoProfileWrapper
import kotlinx.coroutines.flow.Flow

interface InfoUserRepository {

    val myPersonalInfoData: Flow<PersonalInfoData?>
    suspend fun updatePersonalInfo(updateInfoProfileWrapper: UpdateInfoProfileWrapper)

    suspend fun getPersonalInfo()

}