package com.nullpointer.nullsiteadmin.domain.image

import android.net.Uri
import com.nullpointer.nullsiteadmin.core.states.StorageTaskResult
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun uploadImageProfile(uri: Uri, idUser: String): Flow<StorageTaskResult>
    suspend fun compressImg(newImg: Uri): Uri
}