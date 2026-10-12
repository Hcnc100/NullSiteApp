package com.nullpointer.nullsiteadmin.models.project.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.models.project.wrapper.UpdateProjectWrapper
import kotlinx.serialization.Serializable
import com.nullpointer.nullsiteadmin.models.project.data.ProjectContent
import com.nullpointer.nullsiteadmin.models.project.data.ProjectContentContract
import com.google.firebase.firestore.FieldValue

@Serializable
data class UpdateProjectDTO(
    val name: String?,
    val description: String?,
    val urlImg: String?,
    val urlRepo: String?,
    val published: Boolean?,
    val content: ProjectContent? = null,
    val originalContent: ProjectContent = ProjectContent(),
): MappableFirebase{
    override fun toCreateMap(): Map<String, Any> = toUpdateMap().filterKeys { !it.startsWith("translations.") } +
        (content?.let(ProjectContentContract::create).orEmpty()) + ("createdAt" to FieldValue.serverTimestamp())
    override fun toUpdateMap(): Map<String, Any> = buildMap {
        name?.let { put("name", it) }; description?.let { put("description", it) }
        urlImg?.let { put("urlImg", it) }; urlRepo?.let { put("urlRepo", it) }
        published?.let { put("published", it) }
        content?.let { putAll(ProjectContentContract.patch(it, originalContent)) }
        put("updatedAt", FieldValue.serverTimestamp())
    }
    companion object{
        fun fromUpdateProjectWrapper(
            updateProjectWrapper: UpdateProjectWrapper
        ):UpdateProjectDTO{
            return UpdateProjectDTO(
                name = updateProjectWrapper.name,
                urlImg = updateProjectWrapper.urlImg,
                urlRepo = updateProjectWrapper.urlRepo,
                published = updateProjectWrapper.isVisible,
                description = updateProjectWrapper.description,
                content = updateProjectWrapper.content,
                originalContent = updateProjectWrapper.baseline.content,
            )
        }
    }
}
