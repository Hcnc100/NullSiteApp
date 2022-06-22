package com.nullpointer.nullsiteadmin.domain

import com.nullpointer.nullsiteadmin.data.remote.InfoUserDataSource
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import kotlinx.coroutines.flow.Flow

class InfoUserRepoImpl(
    private val infoUserDataSource: InfoUserDataSource
):InfoUserRepository {
    override val myPersonalInfo: Flow<PersonalInfo> =
        infoUserDataSource.getMyInfo()


    override suspend fun updateAnyFieldUser(
        nameAdmin: String?,
        profession: String?,
        description: String?
    ) =  infoUserDataSource.updateAnyInfo(nameAdmin, profession, description)
}