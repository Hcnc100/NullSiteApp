package com.nullpointer.nullsiteadmin.data.remote.project

import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.flow.Flow

interface ProjectDatSource {
    fun getListProject(): Flow<List<Project>>
    suspend fun editProject(project: Project): Flow<Project>
}