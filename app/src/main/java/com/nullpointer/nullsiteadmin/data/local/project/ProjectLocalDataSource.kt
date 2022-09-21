package com.nullpointer.nullsiteadmin.data.local.project

import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.flow.Flow

interface ProjectLocalDataSource {
    val listProject: Flow<List<Project>>

    suspend fun updateProject(project: Project)
    suspend fun insertProject(project: Project)
    suspend fun insertListProjects(listProject: List<Project>)
    suspend fun getMoreRecentProject(): Project?
    suspend fun getLastProject(): Project?
    suspend fun updateAllProjects(listProject: List<Project>)
    suspend fun deleteProjectById(idProject: String)
    suspend fun deleteListProjectById(listIdsProject: List<String>)
}