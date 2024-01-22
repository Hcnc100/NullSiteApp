package com.nullpointer.nullsiteadmin.domain.project

import com.nullpointer.nullsiteadmin.core.utils.callApiTimeOut
import com.nullpointer.nullsiteadmin.datasource.project.local.ProjectLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.project.remote.ProjectRemoteDataSource
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.models.project.dto.CreateProjectDTO
import com.nullpointer.nullsiteadmin.models.project.dto.UpdateProjectDTO
import com.nullpointer.nullsiteadmin.models.project.wrapper.CreateProjectWrapper
import com.nullpointer.nullsiteadmin.models.project.wrapper.UpdateProjectWrapper
import kotlinx.coroutines.flow.Flow

class ProjectRepoImpl(
    private val projectLocalDataSource: ProjectLocalDataSource,
    private val projectRemoteDataSource: ProjectRemoteDataSource
) : ProjectRepository {

    companion object {
        private const val SIZE_REQUEST_PROJECT = 5L
        private const val SIZE_CONCATENATE_PROJECT = 5L
    }

    override val listProjects: Flow<List<ProjectData>> = projectLocalDataSource.listProjectData

    override suspend fun editProject(
        updateProjectWrapper: UpdateProjectWrapper
    ) {
        val updateProjectDTO = UpdateProjectDTO.fromUpdateProjectWrapper(updateProjectWrapper)
        val projectUpdate = projectRemoteDataSource.editProject(
            idProject = updateProjectWrapper.idProject,
            updateProjectDTO = updateProjectDTO
        )
        projectLocalDataSource.updateProject(projectUpdate)

    }

    override suspend fun insertProject(
        createProjectWrapper: CreateProjectWrapper
    ) {
        val createProjectWrapper = CreateProjectDTO.fromCreateProjectWrapper(createProjectWrapper)
        val projectUpdate = projectRemoteDataSource.addNewProject(createProjectWrapper)
        projectLocalDataSource.insertProject(projectUpdate)
    }

    override suspend fun deleterListProjectById(listIdProject: List<String>) {
        projectRemoteDataSource.deleterListProjectById(listIdProject)
        projectLocalDataSource.deleteListProjectById(listIdProject)
    }

    override suspend fun deleterProjectById(idProject: String) {
        projectRemoteDataSource.deleterProject(idProject)
        projectLocalDataSource.deleteProjectById(idProject)
    }

    override suspend fun requestLastProject(): Int {
        val newProjects = projectRemoteDataSource.getLastProjects(SIZE_REQUEST_PROJECT)
        projectLocalDataSource.updateAllProjects(newProjects)
        return newProjects.size
    }

    override suspend fun concatenateProjects(): Int {
        val project = projectLocalDataSource.getLastProject()
        return if (project != null) {
            val newProjects = projectRemoteDataSource.concatenateProject(
                startWithId = project.idProject,
                SIZE_CONCATENATE_PROJECT
            )
            projectLocalDataSource.insertListProjects(newProjects)
            newProjects.size
        } else {
            0
        }
    }
}