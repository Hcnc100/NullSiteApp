package com.nullpointer.nullsiteadmin.data.remote.project

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.core.utils.*
import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class ProjectRemoteDataSourceImpl : ProjectRemoteDataSource {
    companion object {
        private const val COLLECTION_PROJECTS = "last-projects"
        private const val TIMESTAMP_CREATE = "createdAt"
        private const val TIMESTAMP_UPDATE = "lastUpdate"
        private const val ID_PROJECT = "idProject"
    }

    private val refProjects = Firebase.firestore.collection(COLLECTION_PROJECTS)


    override suspend fun insertProject(project: Project): Project? {
        val projectMap = project.toMap(
            listTimestampFields = listOf(TIMESTAMP_UPDATE, TIMESTAMP_CREATE),
            listIgnoredFields = listOf(ID_PROJECT)
        )
        val document = refProjects.add(projectMap).await()
        return fromDocument(document.get().await())
    }

    override suspend fun editProject(project: Project): Project? {
        val projectMap = project.toMap(
            listTimestampFields = listOf(TIMESTAMP_UPDATE),
            listIgnoredFields = listOf(ID_PROJECT)
        )
        val refCurrentProject = refProjects.document(project.idProject)
        refCurrentProject.update(projectMap).await()
        return fromDocument(refCurrentProject.get().await())
    }

    override suspend fun deleterProject(idProject: String) {
        refProjects.document(idProject).delete().await()
    }

    override suspend fun deleterListProjectById(listIdsProject: List<String>) {
        val listOperations = listIdsProject.map {
            refProjects.document(it).delete()
        }
        listOperations.awaitAll()
    }

    override suspend fun getMoreRecentProject(
        includeStart: Boolean,
        startWithId: String?,
        numberResult: Long
    ): List<Project> {
        return refProjects.getNewObjects(
            nResults = numberResult,
            endWithId = startWithId,
            includeEnd = includeStart,
            transform = ::fromDocument,
            fieldTimestamp = TIMESTAMP_UPDATE,
        )
    }

    override suspend fun getConcatenatePost(
        includeStart: Boolean,
        startWithId: String?,
        numberResult: Long
    ): List<Project> {
        return refProjects.getConcatenateObjects(
            includeStart = includeStart,
            nResults = numberResult,
            startWithId = startWithId,
            transform = ::fromDocument,
            fieldTimestamp = TIMESTAMP_UPDATE,
        )
    }

    private fun fromDocument(document: DocumentSnapshot): Project? {
        return try {
            document.toObject<Project>()?.copy(
                lastUpdate = document.getTimeEstimate(TIMESTAMP_UPDATE),
                createdAt = document.getTimeEstimate(TIMESTAMP_CREATE),
                idProject = document.id
            )
        } catch (e: Exception) {
            Timber.e("Error cast ${document.id} to email")
            null
        }
    }
}


