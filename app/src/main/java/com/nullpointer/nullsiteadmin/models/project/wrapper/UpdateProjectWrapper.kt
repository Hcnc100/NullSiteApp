package com.nullpointer.nullsiteadmin.models.project.wrapper

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.database.DateAsLongSerializer
import com.nullpointer.nullsiteadmin.models.project.dto.CreateProjectDTO
import kotlinx.serialization.Serializable
import java.util.Date


data class UpdateProjectWrapper(
    val idProject: String,
    val name: String?,
    val description: String?,
    val urlImg: String?,
    val urlRepo: String?,
    val isVisible: Boolean?
)
