package com.nullpointer.nullsiteadmin.datasource.image.remote

import android.net.Uri
import com.nullpointer.nullsiteadmin.core.states.StorageTaskResult
import kotlinx.coroutines.flow.Flow

interface ImageRemoteDataSource {

    suspend fun uploadImageProfile(
        uriImg: Uri,
        idUser: String
    ): String

    fun uploadImageProfileTask(
        uriImg: Uri,
        idUser: String
    ): Flow<StorageTaskResult>
}