package com.nullpointer.nullsiteadmin.datasource.project.remote

import com.nullpointer.nullsiteadmin.core.utils.callApiTimeOut
import com.nullpointer.nullsiteadmin.data.project.remote.ProjectApiServices
import com.nullpointer.nullsiteadmin.models.project.dto.CreateProjectDTO
import com.nullpointer.nullsiteadmin.models.project.dto.UpdateProjectDTO

class ProjectRemoteDataSourceImpl(
    private val projectApiServices: ProjectApiServices
):ProjectRemoteDataSource {
    override suspend fun addNewProject(
        createProjectDTO: CreateProjectDTO
    ) = callApiTimeOut {
        projectApiServices.addNewProject(createProjectDTO)
    }

    override suspend fun editProject(
        idProject: String,
        updateProjectDTO: UpdateProjectDTO
    )= callApiTimeOut {
        projectApiServices.editProject(idProject, updateProjectDTO)
    }

    override suspend fun deleterProject(
        idProject: String
    ) = callApiTimeOut {
        projectApiServices.deleterProject(idProject)
    }

    override suspend fun deleterListProjectById(
        listIdsProject: List<String>
    ) = callApiTimeOut {
        projectApiServices.deleterListProjectById(listIdsProject)
    }

    override suspend fun getLastProjects(
        numberResult: Long
    ) = callApiTimeOut {
            projectApiServices.getLastProjects(numberResult)
        }

    override suspend fun concatenateProject(
        startWithId: String,
        numberResult: Long
    ) = callApiTimeOut {
        projectApiServices.concatenateProject(
            startWithId,
            numberResult
        )
    }
}