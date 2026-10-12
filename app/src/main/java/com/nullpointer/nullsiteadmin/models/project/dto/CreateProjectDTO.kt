package com.nullpointer.nullsiteadmin.models.project.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.models.project.wrapper.CreateProjectWrapper
import kotlinx.serialization.Serializable

@Serializable
data class CreateProjectDTO(
    val name: String,
    val urlImg: String,
    val urlRepo: String,
    val published: Boolean,
    val description: String,
    val content: com.nullpointer.nullsiteadmin.models.project.data.ProjectContent = com.nullpointer.nullsiteadmin.models.project.data.ProjectContent(),
): MappableFirebase {
    override fun toUpdateMap(): Map<String, Any> = toCreateMap() - "createdAt"
    override fun toCreateMap(): Map<String, Any> = mapOf(
        "name" to name, "urlImg" to urlImg, "urlRepo" to urlRepo, "published" to published,
        "description" to description,
        "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
        "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
    ) + com.nullpointer.nullsiteadmin.models.project.data.ProjectContentContract.create(content)
    companion object{
        fun fromCreateProjectWrapper(
            createProjectWrapper: CreateProjectWrapper
        ):CreateProjectDTO{
            return CreateProjectDTO(
                name = createProjectWrapper.name,
                urlImg = createProjectWrapper.urlImg,
                urlRepo = createProjectWrapper.urlRepo,
                published = createProjectWrapper.isVisible,
                description = createProjectWrapper.description,
                content = createProjectWrapper.content,
            )
        }
    }
}
