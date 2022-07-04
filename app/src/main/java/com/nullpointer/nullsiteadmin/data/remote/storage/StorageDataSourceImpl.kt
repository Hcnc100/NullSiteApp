package com.nullpointer.nullsiteadmin.data.remote.storage

import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.nullpointer.nullsiteadmin.core.states.StorageTaskResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.absoluteValue


class StorageDataSourceImpl : StorageDataSource {
    companion object {
        private const val REF_IMG_PROFILE = "imgProfile"
    }

    private val refImgProfile = Firebase.storage.getReference(REF_IMG_PROFILE)
    private val uuid get() = Firebase.auth.currentUser?.uid ?: "unknown"

    override fun uploadImageProfile(uriImg: Uri) = callbackFlow {
        refImgProfile.child(uuid).putFile(uriImg).addOnSuccessListener { task ->
            task.storage.downloadUrl.addOnSuccessListener {
                trySend(StorageTaskResult.Complete.Success(it.toString()))
                close()
            }.addOnFailureListener {
                trySend(StorageTaskResult.Complete.Failed(it))
                close()
            }
        }
            .addOnFailureListener {
                trySend(StorageTaskResult.Complete.Failed(it))
                close(it)
            }
            .addOnPausedListener {
                trySend(StorageTaskResult.Paused(it))
            }
            .addOnProgressListener { task ->
                val progressResult = StorageTaskResult.InProgress(task.percentTransfer)
                trySend(progressResult)
            }
            .addOnCanceledListener {
                trySend(StorageTaskResult.Complete.Cancelled)
                close(CancellationException("Download was cancelled"))
            }
        awaitClose()
    }
}


private val UploadTask.TaskSnapshot.percentTransfer: Float
    get() = (bytesTransferred.toFloat() / totalByteCount.absoluteValue.toFloat()) * 100

