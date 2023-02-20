package com.nullpointer.nullsiteadmin.data.remote.storage

import android.net.Uri
import com.nullpointer.nullsiteadmin.core.states.StorageTaskResult
import kotlinx.coroutines.flow.Flow

interface StorageDataSource {

     fun uploadImageProfile(uriImg: Uri, idUser: String): Flow<StorageTaskResult>
}