package com.nullpointer.nullsiteadmin.core.states

import com.google.firebase.storage.UploadTask

sealed class StorageTaskResult {

    data class InProgress(val percent: Float) : StorageTaskResult()
    data class Paused(val task: UploadTask.TaskSnapshot) : StorageTaskResult()
    object Init : StorageTaskResult()

    sealed class Complete : StorageTaskResult() {
        data class Success(val urlFile: String) : Complete()
        data class Failed(val error: Throwable) : Complete()
        object Cancelled : Complete()
    }
}