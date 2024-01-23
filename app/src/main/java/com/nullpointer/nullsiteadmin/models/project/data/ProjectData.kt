package com.nullpointer.nullsiteadmin.models.project.data

import com.nullpointer.nullsiteadmin.database.DateAsLongSerializer
import com.nullpointer.nullsiteadmin.models.project.entity.ProjectEntity
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class ProjectData(
    val idProject: String = "",
    val name: String = "",
    val description: String = "",
    val urlImg: String = "",
    val urlRepo: String = "",
    @Serializable(with = DateAsLongSerializer::class)
    val createdAt: Date? = null,
    @Serializable(with = DateAsLongSerializer::class)
    val updatedAt: Date? = null,
    val isVisible: Boolean = false
){
    companion object {

        val example = ProjectData(
            idProject = "1",
            name = "NullSite",
            description = "lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud",
        )

        val exampleList = listOf(
            example,
            example.copy(idProject = "2"),
            example.copy(idProject = "3"),
            example.copy(idProject = "4"),
            example.copy(idProject = "5"),
            example.copy(idProject = "6"),
            example.copy(idProject = "7"),
            example.copy(idProject = "8"),
            example.copy(idProject = "9"),
        )

        fun fromProjectEntity(projectEntity: ProjectEntity) = ProjectData(
            idProject = projectEntity.idProject,
            name = projectEntity.name,
            description = projectEntity.description,
            urlImg = projectEntity.urlImg,
            urlRepo = projectEntity.urlRepo,
            createdAt = projectEntity.createdAt,
            isVisible = projectEntity.isVisible,
            updatedAt = projectEntity.updatedAt
        )
    }
}