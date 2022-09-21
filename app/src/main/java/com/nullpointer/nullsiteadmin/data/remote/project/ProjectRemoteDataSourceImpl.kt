package com.nullpointer.nullsiteadmin.data.remote.project

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.core.utils.awaitAll
import com.nullpointer.nullsiteadmin.core.utils.concatenateObjects
import com.nullpointer.nullsiteadmin.core.utils.getTimeEstimate
import com.nullpointer.nullsiteadmin.core.utils.newObjects
import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class ProjectDataSourceImpl : ProjectRemoteDataSource {
    companion object {
        private const val COLLECTION_PROJECTS = "last-projects"
        private const val TIMESTAMP_CREATE = "createdAt"
        private const val TIMESTAMP_UPDATE = "lastUpdate"
    }

    private val refProjects = Firebase.firestore.collection(COLLECTION_PROJECTS)

    override fun getListProject(): Flow<List<Project>> = callbackFlow {
        val listener = refProjects.addSnapshotListener { value, error ->
            error?.let { channel.close(it) }
            try {
                val list = value!!.documents.mapNotNull(::fromDocument)
                trySend(list)
            } catch (e: Exception) {
                channel.close(e)
            }
        }
        awaitClose { listener.remove() }
    }


    override suspend fun insertProject(project: Project): String {
        val document = refProjects.add(project).await()
        return document.id
    }

    override suspend fun editProject(project: Project) {
        refProjects.document(project.id).update(project.toMap()).await()
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
        return refProjects.newObjects(
            includeEnd = false,
            fieldTimestamp = TIMESTAMP_CREATE,
            endWithId = startWithId,
            nResults = numberResult,
            transform = ::fromDocument,
        )
    }

    override suspend fun getConcatenatePost(
        includeStart: Boolean,
        startWithId: String?,
        numberResult: Long
    ): List<Project> {
        return refProjects.concatenateObjects(
            includeStart = true,
            fieldTimestamp = TIMESTAMP_CREATE,
            startWithId = startWithId,
            nResults = numberResult,
            transform = ::fromDocument,
        )
    }

    private fun fromDocument(document: DocumentSnapshot): Project? {
        return try {
            document.toObject<Project>()?.copy(
                lastUpdate = document.getTimeEstimate(TIMESTAMP_UPDATE),
                createdAt = document.getTimeEstimate(TIMESTAMP_CREATE),
                id = document.id
            )
        } catch (e: Exception) {
            Timber.e("Error cast ${document.id} to email")
            null
        }
    }


    private fun Project.toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "description" to description,
            "urlRepo" to urlRepo,
            "urlImg" to urlImg,
            "lastUpdate" to FieldValue.serverTimestamp(),
            "isVisible" to isVisible
        )
    }
}


