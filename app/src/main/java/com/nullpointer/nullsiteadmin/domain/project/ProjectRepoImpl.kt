package com.nullpointer.nullsiteadmin.domain.project

import com.nullpointer.nullsiteadmin.data.remote.project.ProjectRemoteDataSource
import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.flow.Flow

class ProjectRepoImpl(
    private val projectDataSource: ProjectRemoteDataSource
) : ProjectRepository {
    override val listProjects: Flow<List<Project>> =
        projectDataSource.getListProject()

    override suspend fun editProject(project: Project) =
        projectDataSource.editProject(project)
}