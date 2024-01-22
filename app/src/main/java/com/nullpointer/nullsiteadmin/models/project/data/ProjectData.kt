package com.nullpointer.nullsiteadmin.models.project.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.nullpointer.nullsiteadmin.database.DateAsLongSerializer
import com.nullpointer.nullsiteadmin.models.project.entity.ProjectEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

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
    val lastUpdate: Date? = null,
    val isVisible: Boolean = false
){
    companion object{
        fun fromProjectEntity(projectEntity: ProjectEntity) = ProjectData(
            idProject = projectEntity.idProject,
            name = projectEntity.name,
            description = projectEntity.description,
            urlImg = projectEntity.urlImg,
            urlRepo = projectEntity.urlRepo,
            createdAt = projectEntity.createdAt,
            lastUpdate = projectEntity.lastUpdate,
            isVisible = projectEntity.isVisible
        )
    }
}