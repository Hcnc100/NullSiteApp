package com.nullpointer.nullsiteadmin.data.remote.project

import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRemoteDataSource {
    fun getListProject(): Flow<List<Project>>
    suspend fun insertProject(project: Project): String
    suspend fun editProject(project: Project)
    suspend fun deleterProject(idProject: String)
    suspend fun deleterListProjectById(listIdsProject: List<String>)
    suspend fun getMoreRecentProject(
        includeStart: Boolean,
        startWithId: String?,
        numberResult: Long
    ): List<Project>

    suspend fun getConcatenatePost(
        includeStart: Boolean,
        startWithId: String?,
        numberResult: Long
    ): List<Project>
}