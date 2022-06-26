package com.nullpointer.nullsiteadmin.domain.project

import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    val listProjects: Flow<List<Project>>
    suspend fun editProject(project: Project)
}