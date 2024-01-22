package com.nullpointer.nullsiteadmin.domain.project

import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.models.project.dto.CreateProjectDTO
import com.nullpointer.nullsiteadmin.models.project.wrapper.CreateProjectWrapper
import com.nullpointer.nullsiteadmin.models.project.wrapper.UpdateProjectWrapper
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    val listProjects: Flow<List<ProjectData>>
    suspend fun editProject(
        updateProjectWrapper: UpdateProjectWrapper
    )
    suspend fun insertProject(
        createProjectWrapper: CreateProjectWrapper
    )
    suspend fun deleterListProjectById(listIdProject: List<String>)
    suspend fun deleterProjectById(idProject: String)
    suspend fun requestLastProject(): Int
    suspend fun concatenateProjects(): Int
}