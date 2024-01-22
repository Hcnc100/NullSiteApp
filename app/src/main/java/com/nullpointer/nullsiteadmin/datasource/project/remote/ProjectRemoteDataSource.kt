package com.nullpointer.nullsiteadmin.datasource.project.remote

import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.models.project.dto.CreateProjectDTO
import com.nullpointer.nullsiteadmin.models.project.dto.UpdateProjectDTO

interface ProjectRemoteDataSource {

    suspend fun addNewProject(createProjectDTO: CreateProjectDTO):ProjectData

    suspend fun editProject(idProject: String, updateProjectDTO: UpdateProjectDTO):ProjectData

    suspend fun deleterProject(idProject: String)

    suspend fun deleterListProjectById(listIdsProject: List<String>)

    suspend fun getLastProjects(numberResult: Long):List<ProjectData>

    suspend fun concatenateProject(
        startWithId: String,
        numberResult: Long
    ):List<ProjectData>
}