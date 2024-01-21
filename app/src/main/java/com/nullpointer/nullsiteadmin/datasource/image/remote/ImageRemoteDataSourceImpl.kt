package com.nullpointer.nullsiteadmin.datasource.image.remote

import android.net.Uri
import com.nullpointer.nullsiteadmin.core.states.StorageTaskResult
import com.nullpointer.nullsiteadmin.data.image.remote.ImageApiServices
import kotlinx.coroutines.flow.Flow

class ImageRemoteDataSourceImpl(
    private val imageApiServices: ImageApiServices
):ImageRemoteDataSource {
    override suspend fun uploadImageProfile(
        uriImg: Uri,
        idUser: String
    ): String  = imageApiServices.uploadImageProfile(
        uriImg = uriImg,
        idUser = idUser
    )

    override fun uploadImageProfileTask(uriImg: Uri, idUser: String): Flow<StorageTaskResult>
        = imageApiServices.uploadImageProfileTask(
        uriImg = uriImg,
        idUser = idUser
    )
}