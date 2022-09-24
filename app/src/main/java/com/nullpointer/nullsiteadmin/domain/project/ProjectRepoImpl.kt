package com.nullpointer.nullsiteadmin.domain.project

import com.nullpointer.nullsiteadmin.data.local.project.ProjectLocalDataSource
import com.nullpointer.nullsiteadmin.data.remote.project.ProjectRemoteDataSource
import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.flow.Flow

class ProjectRepoImpl(
    private val projectLocalDataSource: ProjectLocalDataSource,
    private val projectRemoteDataSource: ProjectRemoteDataSource
) : ProjectRepository {

    companion object {
        private const val SIZE_REQUEST_PROJECT = 5L
        private const val SIZE_CONCATENATE_PROJECT = 5L
    }

    override val listProjects: Flow<List<Project>> = projectLocalDataSource.listProject

    override suspend fun editProject(project: Project) {
        val projectUpdate = projectRemoteDataSource.editProject(project)
        projectUpdate?.let { projectLocalDataSource.updateProject(project) }

    }

    override suspend fun insertProject(project: Project) {
        val projectUpdate = projectRemoteDataSource.insertProject(project)
        projectUpdate?.let { projectLocalDataSource.insertProject(project) }

    }

    override suspend fun deleterListProjectById(listIdProject: List<String>) {
        projectRemoteDataSource.deleterListProjectById(listIdProject)
        projectLocalDataSource.deleteListProjectById(listIdProject)
    }

    override suspend fun deleterProjectById(idProject: String) {
        projectRemoteDataSource.deleterProject(idProject)
        projectLocalDataSource.deleteProjectById(idProject)
    }

    override suspend fun requestLastProject(forceRefresh: Boolean): Int {
        val project = projectLocalDataSource.getMoreRecentProject()
        val idProject = if (forceRefresh) null else project?.idProject
        val newProjects = projectRemoteDataSource.getMoreRecentProject(
            includeStart = false,
            startWithId = idProject,
            SIZE_REQUEST_PROJECT
        )
        if (newProjects.isNotEmpty()) projectLocalDataSource.updateAllProjects(newProjects)
        return newProjects.size
    }

    override suspend fun concatenateProjects(): Int {
        val project = projectLocalDataSource.getLastProject()
        return if (project != null) {
            val newProjects = projectRemoteDataSource.getConcatenatePost(
                includeStart = false,
                startWithId = project.idProject,
                SIZE_CONCATENATE_PROJECT
            )
            if (newProjects.isNotEmpty()) projectLocalDataSource.insertListProjects(newProjects)
            newProjects.size
        } else {
            0
        }
    }
}