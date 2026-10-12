package com.nullpointer.nullsiteadmin.models.project.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.nullpointer.nullsiteadmin.models.project.data.ProjectContentContract
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
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
    val updatedAt: Date,
    val isVisible: Boolean,
    @ColumnInfo(defaultValue = "'{}'") val contentJson: String = "{}",
){
    companion object{
        fun fromProjectData(projectData: ProjectData) = ProjectEntity(
            idProject = projectData.idProject,
            name = projectData.name,
            description = projectData.description,
            urlImg = projectData.urlImg,
            urlRepo = projectData.urlRepo,
            createdAt = projectData.createdAt!!,
            updatedAt = projectData.updatedAt!!,
            isVisible = projectData.isVisible,
            contentJson = ProjectContentContract.encode(projectData.content),
        )
    }
}
