package com.nullpointer.nullsiteadmin.domain.project

import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    val listProjects: Flow<List<Project>>
    suspend fun editProject(project: Project)
    suspend fun insertProject(project: Project)
    suspend fun deleterListProjectById(listIdProject: List<String>)
    suspend fun deleterProjectById(idProject: String)
    suspend fun requestLastProject(forceRefresh: Boolean): Int
    suspend fun concatenateProjects(): Int
}