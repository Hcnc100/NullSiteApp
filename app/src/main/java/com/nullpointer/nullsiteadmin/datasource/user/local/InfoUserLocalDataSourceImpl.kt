package com.nullpointer.nullsiteadmin.datasource.user.local

import com.nullpointer.nullsiteadmin.data.user.local.UserDataStore
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import kotlinx.coroutines.flow.Flow

class InfoUserLocalDataSourceImpl(
    private val userDataStore: UserDataStore
) : InfoUserLocalDataSource {


    override fun getPersonalInfo():Flow<PersonalInfoData?>  =
        userDataStore.getPersonalInfo()

    override suspend fun updatePersonalInfo(
        newPersonalInfoData: PersonalInfoData
    ) = userDataStore.updatePersonalInfo(newPersonalInfoData)



    override suspend fun deleterPersonalInfo()= userDataStore.deleterPersonalInfo()

}