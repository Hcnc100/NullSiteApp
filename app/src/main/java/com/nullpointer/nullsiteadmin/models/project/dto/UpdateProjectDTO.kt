package com.nullpointer.nullsiteadmin.models.project.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.models.project.wrapper.UpdateProjectWrapper
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProjectDTO(
    val name: String?,
    val description: String?,
    val urlImg: String?,
    val urlRepo: String?,
    val isVisible: Boolean?
): MappableFirebase{
    companion object{
        fun fromUpdateProjectWrapper(
            updateProjectWrapper: UpdateProjectWrapper
        ):UpdateProjectDTO{
            return UpdateProjectDTO(
                name = updateProjectWrapper.name,
                urlImg = updateProjectWrapper.urlImg,
                urlRepo = updateProjectWrapper.urlRepo,
                isVisible = updateProjectWrapper.isVisible,
                description = updateProjectWrapper.description
            )
        }
    }
}