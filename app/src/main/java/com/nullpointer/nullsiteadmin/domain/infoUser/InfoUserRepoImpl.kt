package com.nullpointer.nullsiteadmin.domain.infoUser

import android.net.Uri
import com.nullpointer.nullsiteadmin.core.utils.callApiTimeOut
import com.nullpointer.nullsiteadmin.datasource.user.local.InfoUserLocalDataSource
import com.nullpointer.nullsiteadmin.data.local.services.ServicesManager
import com.nullpointer.nullsiteadmin.datasource.auth.local.AuthLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.user.remote.InfoUserRemoteDataSource
import com.nullpointer.nullsiteadmin.models.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.dto.PersonalInfoDTO
import com.nullpointer.nullsiteadmin.models.wrapper.InfoProfileWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class InfoUserRepoImpl(
    private val authLocalDataSource: AuthLocalDataSource,
    private val infoUserLocalDataSource: InfoUserLocalDataSource,
    private val infoUserRemoteDataSource: InfoUserRemoteDataSource,
) : InfoUserRepository {

    override val myPersonalInfoData: Flow<PersonalInfoData?> =
        infoUserLocalDataSource.getPersonalInfo()

    override suspend fun updatePersonalInfo(
        infoProfileWrapper: InfoProfileWrapper
    ) {
        val idUser = infoUserLocalDataSource.getPersonalInfo().first()!!.id
        val personalInfoDTO = PersonalInfoDTO.fromPersonalInfoWrapper(infoProfileWrapper)
        callApiTimeOut {
            infoUserRemoteDataSource.updatePersonalInfo(
                idUser = idUser,
                personalInfoDTO = personalInfoDTO
            )
        }
        val newData = callApiTimeOut {
            infoUserRemoteDataSource.getPersonalInfo(idUser)
        }
        infoUserLocalDataSource.updatePersonalInfo(newData!!)
        infoProfileWrapper.uriImageProfile?.let {
            //! TODO add upload image
        }
    }

    override suspend fun getPersonalInfo() {
        val authData= authLocalDataSource.getAuthData().first()
        val newData = callApiTimeOut {
            infoUserRemoteDataSource.getPersonalInfo(authData!!.id)
        }
        infoUserLocalDataSource.updatePersonalInfo(newData!!)
    }

}