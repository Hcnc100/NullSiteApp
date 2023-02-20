package com.nullpointer.nullsiteadmin.domain.storage

import android.net.Uri
import com.nullpointer.nullsiteadmin.data.local.compress.ImageCompressDataSource
import com.nullpointer.nullsiteadmin.data.remote.storage.StorageDataSource

class ImageRepositoryImpl(
    private val storageDataSource: StorageDataSource,
    private val imageCompressDataSource: ImageCompressDataSource
) : ImageRepository {
    override fun uploadImageProfile(uri: Uri, idUser: String) =
        storageDataSource.uploadImageProfile(uri, idUser)

    override suspend fun compressImg(newImg: Uri): Uri =
        imageCompressDataSource.compressImg(newImg)
}