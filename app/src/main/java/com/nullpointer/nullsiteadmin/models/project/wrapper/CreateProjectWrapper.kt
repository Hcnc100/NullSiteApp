package com.nullpointer.nullsiteadmin.models.project.wrapper

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import kotlinx.serialization.Serializable

data class CreateProjectWrapper(
    val name: String,
    val urlImg: String,
    val urlRepo: String,
    val isVisible: Boolean,
    val description: String,
)