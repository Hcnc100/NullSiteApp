package com.nullpointer.nullsiteadmin.domain.image

import android.net.Uri
import com.nullpointer.nullsiteadmin.models.project.data.ProjectImage

interface ProjectImageUploader {
    suspend fun upload(projectId: String, uri: Uri, progress: (Float) -> Unit): ProjectImage
}

object UnavailableProjectImageUploader : ProjectImageUploader {
    override suspend fun upload(projectId: String, uri: Uri, progress: (Float) -> Unit): ProjectImage =
        error("No uploader configured")
}
