package com.nullpointer.nullsiteadmin.domain.infoUser

import android.net.Uri
import com.nullpointer.nullsiteadmin.models.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.wrapper.InfoProfileWrapper
import kotlinx.coroutines.flow.Flow

interface InfoUserRepository {

    val myPersonalInfoData: Flow<PersonalInfoData?>
    suspend fun updatePersonalInfo( infoProfileWrapper: InfoProfileWrapper)

    suspend fun getPersonalInfo()

}