package com.nullpointer.nullsiteadmin.domain.infoUser

import com.nullpointer.nullsiteadmin.data.remote.infoUser.InfoUserDataSource
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import kotlinx.coroutines.flow.Flow

class InfoUserRepoImpl(
    private val infoUserDataSource: InfoUserDataSource
): InfoUserRepository {
    override val myPersonalInfo: Flow<PersonalInfo> =
        infoUserDataSource.getMyInfo()

    override suspend fun updatePersonalInfo(personalInfo: PersonalInfo) =
        infoUserDataSource.updatePersonalInfo(personalInfo)


}