package com.nullpointer.nullsiteadmin.models.project.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.Date

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val idProject: String,
    val name: String,
    val description: String,
    val urlImg: String,
    val urlRepo: String,
    val createdAt: Date,
    val lastUpdate: Date,
    val isVisible: Boolean,
){
    companion object{
        fun fromProjectData(projectData: ProjectData) = ProjectEntity(
            idProject = projectData.idProject,
            name = projectData.name,
            description = projectData.description,
            urlImg = projectData.urlImg,
            urlRepo = projectData.urlRepo,
            createdAt = projectData.createdAt!!,
            lastUpdate = projectData.lastUpdate!!,
            isVisible = projectData.isVisible
        )
    }
}