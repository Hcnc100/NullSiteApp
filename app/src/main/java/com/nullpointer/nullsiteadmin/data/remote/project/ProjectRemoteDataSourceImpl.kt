package com.nullpointer.nullsiteadmin.data.remote.project

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProjectDataSourceImpl : ProjectRemoteDataSource {
    companion object {
        private const val COLLECTION_PROJECTS = "last-projects"
    }

    private val refProjects = Firebase.firestore.collection(COLLECTION_PROJECTS)

    override fun getListProject(): Flow<List<Project>> = callbackFlow {
        val listener = refProjects.addSnapshotListener { value, error ->
            error?.let { channel.close(it) }
            try {
                val list = value!!.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject<Project>()?.copy(
                        id = documentSnapshot.id
                    )
                }
                trySend(list)
            }catch (e: Exception) {
                channel.close(e)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun editProject(project: Project) {
        refProjects.document(project.id).update(project.toMap()).await()
    }
}

private fun Project.toMap(): Map<String, Any> {
    return mapOf(
        "name" to name,
        "description" to description,
        "urlRepo" to urlRepo,
        "urlImg" to urlImg
    )
}
