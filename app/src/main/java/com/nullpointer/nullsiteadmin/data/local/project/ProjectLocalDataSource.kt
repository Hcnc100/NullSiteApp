package com.nullpointer.nullsiteadmin.data.local.project

import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.flow.Flow

interface ProjectLocalDataSource {
    val listProject: Flow<List<Project>>

    suspend fun updateProject(project: Project)
    suspend fun insertProject(project: Project)
}