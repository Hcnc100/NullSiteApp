package com.nullpointer.nullsiteadmin.data.remote.project

import com.nullpointer.nullsiteadmin.models.Project

interface ProjectRemoteDataSource {
    suspend fun insertProject(project: Project): Project?
    suspend fun editProject(project: Project): Project?
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