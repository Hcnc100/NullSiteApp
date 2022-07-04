package com.nullpointer.nullsiteadmin.domain.storage

import android.net.Uri
import com.nullpointer.nullsiteadmin.data.remote.storage.StorageDataSource

class RepoImageProfileImpl(
    private val storageDataSource: StorageDataSource
):RepositoryImageProfile {
    override fun uploadImageProfile(uri: Uri) =
        storageDataSource.uploadImageProfile(uri)
}