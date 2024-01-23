package com.nullpointer.nullsiteadmin.data.project.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.core.utils.Constants
import com.nullpointer.nullsiteadmin.core.utils.awaitAll
import com.nullpointer.nullsiteadmin.core.utils.getTimeEstimate
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.models.project.dto.CreateProjectDTO
import com.nullpointer.nullsiteadmin.models.project.dto.UpdateProjectDTO
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class ProjectApiServices {

    private val refProjects = Firebase.firestore.collection(Constants.PROJECT_COLLECTION)


    suspend fun addNewProject(
        createProjectDTO: CreateProjectDTO
    ): ProjectData {
        val response = refProjects.add(createProjectDTO.toCreateMap()).await()
        val newProject = response.get().await()
        return fromDocument(newProject)!!
    }

    suspend fun editProject(
        idProject: String,
        updateProjectDTO: UpdateProjectDTO
    ):ProjectData {
        refProjects.document(idProject).update(updateProjectDTO.toUpdateMap()).await()
        val newProject = refProjects.document(idProject).get().await()
        return fromDocument(newProject)!!
    }

    suspend fun deleterProject(idProject: String) {
        refProjects.document(idProject).delete().await()
    }

    suspend fun deleterListProjectById(listIdsProject: List<String>) {
        val listOperations = listIdsProject.map {
            refProjects.document(it).delete()
        }
        listOperations.awaitAll()
    }

    suspend fun getLastProjects(
        numberResult: Long
    ): List<ProjectData> {
        val result =
            refProjects.orderBy(Constants.CREATED_AT, Query.Direction.DESCENDING).limit(numberResult)
                .get().await()
        return result.documents.mapNotNull(::fromDocument)
    }

    suspend fun concatenateProject(
        startWithId: String,
        numberResult: Long
    ): List<ProjectData> {
        val lastProjectDoc = refProjects.document(startWithId).get().await()
        val result = refProjects.orderBy(Constants.CREATED_AT, Query.Direction.DESCENDING)
            .startAfter(lastProjectDoc).limit(numberResult).get().await()
        return result.documents.mapNotNull(::fromDocument)
    }

    private fun fromDocument(document: DocumentSnapshot): ProjectData? {
        return try {
            document.toObject<ProjectData>()?.copy(
                updatedAt = document.getTimeEstimate(Constants.UPDATED_AT),
                createdAt = document.getTimeEstimate(Constants.CREATED_AT),
                idProject = document.id
            )
        } catch (e: Exception) {
            Timber.e("Error cast ${document.id} to email $e")
            null
        }
    }
}