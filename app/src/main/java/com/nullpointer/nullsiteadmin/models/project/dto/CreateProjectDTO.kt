package com.nullpointer.nullsiteadmin.models.project.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.models.project.wrapper.CreateProjectWrapper
import kotlinx.serialization.Serializable

@Serializable
data class CreateProjectDTO(
    val name: String,
    val urlImg: String,
    val urlRepo: String,
    val isVisible: Boolean,
    val description: String,
): MappableFirebase {
    companion object{
        fun fromCreateProjectWrapper(
            createProjectWrapper: CreateProjectWrapper
        ):CreateProjectDTO{
            return CreateProjectDTO(
                name = createProjectWrapper.name,
                urlImg = createProjectWrapper.urlImg,
                urlRepo = createProjectWrapper.urlRepo,
                isVisible = createProjectWrapper.isVisible,
                description = createProjectWrapper.description
            )
        }
    }
}
