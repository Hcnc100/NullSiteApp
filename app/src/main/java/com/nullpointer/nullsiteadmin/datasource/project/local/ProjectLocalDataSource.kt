package com.nullpointer.nullsiteadmin.datasource.project.local

import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import kotlinx.coroutines.flow.Flow

interface ProjectLocalDataSource {
    val listProjectData: Flow<List<ProjectData>>

    suspend fun updateProject(projectData: ProjectData)
    suspend fun insertProject(projectData: ProjectData)
    suspend fun insertListProjects(listProjectData: List<ProjectData>)
    suspend fun getMoreRecentProject(): ProjectData?
    suspend fun getLastProject(): ProjectData?
    suspend fun updateAllProjects(listProjectData: List<ProjectData>)
    suspend fun deleteProjectById(idProject: String)
    suspend fun deleteListProjectById(listIdsProject: List<String>)
    suspend fun deleterAllProjects()
}