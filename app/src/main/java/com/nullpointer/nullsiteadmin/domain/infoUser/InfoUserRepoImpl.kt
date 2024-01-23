package com.nullpointer.nullsiteadmin.domain.infoUser

import com.nullpointer.nullsiteadmin.datasource.auth.local.AuthLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.image.remote.ImageRemoteDataSource
import com.nullpointer.nullsiteadmin.datasource.user.local.InfoUserLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.user.remote.InfoUserRemoteDataSource
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.dto.PersonalInfoDTO
import com.nullpointer.nullsiteadmin.models.personalInfo.wrapper.UpdateInfoProfileWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class InfoUserRepoImpl(
    private val authLocalDataSource: AuthLocalDataSource,
    private val imageRemoteDataSource: ImageRemoteDataSource,
    private val infoUserLocalDataSource: InfoUserLocalDataSource,
    private val infoUserRemoteDataSource: InfoUserRemoteDataSource,
) : InfoUserRepository {

    override val myPersonalInfoData: Flow<PersonalInfoData?> =
        infoUserLocalDataSource.getPersonalInfo()

    override suspend fun updatePersonalInfo(
        updateInfoProfileWrapper: UpdateInfoProfileWrapper
    ) {
        val idUser = authLocalDataSource.getAuthData().first()!!.id

        val urlImg = updateInfoProfileWrapper.uriFileImgProfile?.let { file ->
            imageRemoteDataSource.uploadImageProfile(file, idUser)
        }

        val personalInfoDTO = PersonalInfoDTO.fromPersonalInfoWrapper(
            urlImg = urlImg,
            infoProfileWrapper = updateInfoProfileWrapper
        )

        infoUserRemoteDataSource.updatePersonalInfo(
            idUser = idUser,
            personalInfoDTO = personalInfoDTO
        )


        getPersonalInfo()
    }

    override suspend fun getPersonalInfo() {
        val authData = authLocalDataSource.getAuthData().first()
        val newData = infoUserRemoteDataSource.getPersonalInfo(authData!!.id)
        infoUserLocalDataSource.updatePersonalInfo(newData!!)
    }

}