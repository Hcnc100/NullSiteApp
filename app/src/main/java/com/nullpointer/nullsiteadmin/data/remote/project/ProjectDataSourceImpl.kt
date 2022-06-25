package com.nullpointer.nullsiteadmin.data.remote.project

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ProjectDataSourceImpl : ProjectDatSource {
    companion object {
        private const val COLLECTION_PROJECTS = "last-projects"
    }

    private val refProjects = Firebase.firestore.collection(COLLECTION_PROJECTS)

    override fun getListProject(): Flow<List<Project>> = callbackFlow {
        val listener = refProjects.addSnapshotListener { value, error ->
            error?.let { channel.close(it) }
            try {
                val list=value!!.toObjects<Project>()
                trySend(list)
            }catch (e: Exception) {
                channel.close(e)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun editProject(project: Project): Flow<Project> {
        TODO("Not yet implemented")
    }
}