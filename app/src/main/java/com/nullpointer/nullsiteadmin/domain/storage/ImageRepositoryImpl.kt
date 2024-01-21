package com.nullpointer.nullsiteadmin.domain.storage

import android.net.Uri
import com.nullpointer.nullsiteadmin.datasource.image.local.ImageLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.image.remote.ImageRemoteDataSource

class ImageRepositoryImpl(
    private val imageLocalDataSource: ImageLocalDataSource,
    private val imageRemoteDataSource: ImageRemoteDataSource,
) : ImageRepository {
    override fun uploadImageProfile(uri: Uri, idUser: String) =
        imageRemoteDataSource.uploadImageProfileTask(uri, idUser)

    override suspend fun compressImg(newImg: Uri): Uri =
        imageLocalDataSource.compressImage(newImg)
}