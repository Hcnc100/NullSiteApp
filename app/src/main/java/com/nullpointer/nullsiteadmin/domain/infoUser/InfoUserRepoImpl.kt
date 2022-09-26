package com.nullpointer.nullsiteadmin.domain.infoUser

import com.nullpointer.nullsiteadmin.core.utils.callApiTimeOut
import com.nullpointer.nullsiteadmin.data.local.infoUser.InfoUserLocalDataSource
import com.nullpointer.nullsiteadmin.data.remote.infoUser.InfoUserRemoteDataSource
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class InfoUserRepoImpl(
    private val infoUserLocalDataSource: InfoUserLocalDataSource,
    private val infoUserRemoteDataSource: InfoUserRemoteDataSource
): InfoUserRepository {

    override val myPersonalInfo: Flow<PersonalInfo> = infoUserLocalDataSource.getPersonalInfo()

    override suspend fun updatePersonalInfo(personalInfo: PersonalInfo) {
        val updatedPersonalInfo = callApiTimeOut {
            infoUserRemoteDataSource.updatePersonalInfo(personalInfo)
        }
        updatedPersonalInfo?.let { infoUserLocalDataSource.updatePersonalInfo(it) }
    }


    override suspend fun requestLastPersonalInfo(forceRefresh: Boolean): Boolean {
        val lastPersonalInfo =
            if (forceRefresh) null else infoUserLocalDataSource.getPersonalInfo().first()
        val newPersonalInfo = callApiTimeOut {
            infoUserRemoteDataSource.getMoreRecentPersonalInfo(
                timestamp = lastPersonalInfo?.lastUpdate
            )
        }
        newPersonalInfo?.let { infoUserLocalDataSource.updatePersonalInfo(it) }
        return newPersonalInfo != null
    }
}