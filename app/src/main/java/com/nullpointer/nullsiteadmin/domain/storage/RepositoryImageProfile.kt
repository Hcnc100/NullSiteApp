package com.nullpointer.nullsiteadmin.domain.storage

import android.net.Uri
import com.nullpointer.nullsiteadmin.core.states.StorageTaskResult
import kotlinx.coroutines.flow.Flow

interface RepositoryImageProfile {
    fun uploadImageProfile(uri: Uri):Flow<StorageTaskResult>
}