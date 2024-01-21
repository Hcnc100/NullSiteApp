package com.nullpointer.nullsiteadmin.domain.infoUser

import com.nullpointer.nullsiteadmin.core.utils.callApiTimeOut
import com.nullpointer.nullsiteadmin.datasource.user.local.InfoUserLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.auth.local.AuthLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.user.remote.InfoUserRemoteDataSource
import com.nullpointer.nullsiteadmin.domain.storage.ImageRepository
import com.nullpointer.nullsiteadmin.models.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.dto.PersonalInfoDTO
import com.nullpointer.nullsiteadmin.models.wrapper.UpdateInfoProfileWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class InfoUserRepoImpl(
    private val authLocalDataSource: AuthLocalDataSource,
    private val imageRemoteDataSource: InfoUserRemoteDataSource,
    private val infoUserLocalDataSource: InfoUserLocalDataSource,
    private val infoUserRemoteDataSource: InfoUserRemoteDataSource,
) : InfoUserRepository {

    override val myPersonalInfoData: Flow<PersonalInfoData?> =
        infoUserLocalDataSource.getPersonalInfo()

    override suspend fun updatePersonalInfo(
        updateInfoProfileWrapper: UpdateInfoProfileWrapper
    ) {
        val idUser = authLocalDataSource.getAuthData().first()!!.id

       val imageProfile= updateInfoProfileWrapper.imageFile?.let {file->
           // TODO add compress image
//            imageRepository.compressImg(file)
           null
        }

        val personalInfoDTO = PersonalInfoDTO.fromPersonalInfoWrapper(
            urlImg = imageProfile,
            infoProfileWrapper = updateInfoProfileWrapper
        )
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
        updateInfoProfileWrapper.uriFileImgProfile?.let {
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