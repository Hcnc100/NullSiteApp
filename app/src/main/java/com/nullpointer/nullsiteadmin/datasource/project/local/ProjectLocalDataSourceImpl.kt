package com.nullpointer.nullsiteadmin.datasource.project.local

import com.nullpointer.nullsiteadmin.data.project.local.ProjectDAO
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.models.project.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectLocalDataSourceImpl(
    private val projectDAO: ProjectDAO
) : ProjectLocalDataSource {
    override val listProjectData: Flow<List<ProjectData>> = projectDAO.getListProjects().map { list->
        list.map(ProjectData::fromProjectEntity)
    }

    override suspend fun updateProject(projectData: ProjectData) {
        val projectEntity = ProjectEntity.fromProjectData(projectData)
        projectDAO.updateProject(projectEntity)
    }

    override suspend fun insertProject(projectData: ProjectData) {
        val projectEntity = ProjectEntity.fromProjectData(projectData)
        projectDAO.insertProject(projectEntity)
    }

    override suspend fun insertListProjects(listProjectData: List<ProjectData>) {

        val listProjectEntities = listProjectData.map(ProjectEntity::fromProjectData)
        projectDAO.insertListProjects(listProjectEntities)
    }

    override suspend fun getMoreRecentProject(): ProjectData? {
        return projectDAO.getMoreRecentProject()?.let {
            ProjectData.fromProjectEntity(it)
        }
    }

    override suspend fun getLastProject(): ProjectData? {
        return projectDAO.getLastProject()?.let {
            ProjectData.fromProjectEntity(it)
        }
    }

    override suspend fun updateAllProjects(listProjectData: List<ProjectData>) {
        val listProjectEntities = listProjectData.map(ProjectEntity::fromProjectData)
        projectDAO.updateAllProjects(listProjectEntities)
    }

    override suspend fun deleteListProjectById(listIdsProject: List<String>) =
        projectDAO.deleterListProjectById(listIdsProject)

    override suspend fun deleteProjectById(idProject: String) =
        projectDAO.deleterProjectById(idProject)

    override suspend fun deleterAllProjects() =
        projectDAO.deleterAllProjects()
}