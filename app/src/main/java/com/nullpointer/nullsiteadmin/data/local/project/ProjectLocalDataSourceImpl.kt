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
}