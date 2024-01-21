package com.nullpointer.nullsiteadmin.data.image.remote

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.nullpointer.nullsiteadmin.BuildConfig
import com.nullpointer.nullsiteadmin.core.states.StorageTaskResult
import com.nullpointer.nullsiteadmin.core.utils.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import kotlin.math.absoluteValue

class ImageApiServices {
    private val refImgProfile = Firebase.storage.getReference(Constants.REF_IMG_PROFILE)

    fun uploadImageProfileTask(
        uriImg: Uri,
        idUser: String
    ): Flow<StorageTaskResult> = callbackFlow {
        refImgProfile.child(BuildConfig.ID_INFO_PROFILE_FIREBASE).putFile(uriImg)
            .addOnSuccessListener { task ->
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


    suspend fun uploadImageProfile(
        uriImg: Uri,
        idUser: String
    ): String {
        val result =
            refImgProfile.child(BuildConfig.ID_INFO_PROFILE_FIREBASE).putFile(uriImg).await()
        return result.storage.downloadUrl.await().toString()
    }


    private val UploadTask.TaskSnapshot.percentTransfer: Float
        get() = (bytesTransferred.toFloat() / totalByteCount.absoluteValue.toFloat()) * 100

}