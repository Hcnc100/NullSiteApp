package com.nullpointer.nullsiteadmin.data.local.project

import com.nullpointer.nullsiteadmin.data.local.room.ProjectDAO
import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.flow.Flow

class ProjectLocalDataSourceImpl(
    private val projectDAO: ProjectDAO
) : ProjectLocalDataSource {
    override val listProject: Flow<List<Project>> = projectDAO.getListProjects()

    override suspend fun updateProject(project: Project) =
        projectDAO.updateProject(project)

    override suspend fun insertProject(project: Project) =
        projectDAO.insertProject(project)

    override suspend fun insertListProjects(listProject: List<Project>) =
        projectDAO.insertListProjects(listProject)

    override suspend fun getMoreRecentProject(): Project? =
        projectDAO.getMoreRecentProject()

    override suspend fun getLastProject(): Project? =
        projectDAO.getLastProject()

    override suspend fun updateAllProjects(listProject: List<Project>) =
        projectDAO.updateAllProjects(listProject)

    override suspend fun deleteListProjectById(listIdsProject: List<String>) =
        projectDAO.deleterListProjectById(listIdsProject)

    override suspend fun deleteProjectById(idProject: String) =
        projectDAO.deleterProjectById(idProject)
}