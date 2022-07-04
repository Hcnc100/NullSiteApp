package com.nullpointer.nullsiteadmin.services.imageProfile

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.StorageTaskResult
import com.nullpointer.nullsiteadmin.core.utils.showToastMessage
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepository
import com.nullpointer.nullsiteadmin.domain.storage.RepositoryImageProfile
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import com.nullpointer.nullsiteadmin.services.imageProfile.UploadImageServicesControl.KEY_INFO_PROFILE
import com.nullpointer.nullsiteadmin.services.imageProfile.UploadImageServicesControl.KEY_URI_PROFILE
import com.nullpointer.nullsiteadmin.services.imageProfile.UploadImageServicesControl.START_COMMAND
import com.nullpointer.nullsiteadmin.services.imageProfile.UploadImageServicesControl.STOP_COMMAND
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class UploadImageServices : LifecycleService() {

    @Inject
    lateinit var repoImageProfileImpl: RepositoryImageProfile

    @Inject
    lateinit var infoUserRepository: InfoUserRepository

    private var jobUploadTask: Job? = null

    private val notifyHelper by lazy { NotifyUploadImgHelper(this) }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let {
            when (it.action) {
                START_COMMAND -> actionStartCommand(intent)
                STOP_COMMAND -> actionStopCommand()
                else -> Timber.e("Command unknown ${it.action}")
            }
        }
        return START_STICKY
    }

    private fun actionStartCommand(intent: Intent) {
        try {
            // * notify to user that operation has been started
            showToastMessage(R.string.message_init_upload)
            // * get extra parameters
            val uriInfo: Uri = intent.getParcelableExtra(KEY_URI_PROFILE)!!
            val personalInfo = intent.getParcelableExtra<PersonalInfo>(KEY_INFO_PROFILE)!!
            // * init async task
            jobUploadTask = lifecycleScope.launch {
                startUploadImage(uriInfo) { newUrlImg ->
                    // * this action is call when the upload is finished
                    // ? update the info user thi the new url img
                    withContext(Dispatchers.IO){
                        infoUserRepository.updatePersonalInfo(
                            personalInfo.copy(urlImg = newUrlImg)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e("Error to upload info profile $e")
            showToastMessage(R.string.error_upload_info)
        }
    }

    private suspend fun startUploadImage(
        uriImage: Uri,
        actionBeforeUpload: suspend (uri: String) -> Unit,
    ) {

        notifyHelper.startServicesForeground(this)

        repoImageProfileImpl.uploadImageProfile(uriImage).catch { exception ->
            // ! if has Error send error and killer service
            Timber.e("Error upload img flow $exception")
            showToastMessage(R.string.error_upload_info)
            killServices()
        }.flowOn(
            Dispatchers.IO
        ).collect { task ->
            when (task) {
                is StorageTaskResult.Complete.Failed -> {
                    // ! if has Error send error and killer service
                    Timber.e("Error upload img flow ${task.error}")
                    showToastMessage(R.string.error_upload_info)
                    killServices()
                }
                is StorageTaskResult.Complete.Success -> {
                    // * when the upload is complete, update the notification
                    // * and upload the personal info
                    notifyHelper.updateNotifyFinishUpdate()
                    actionBeforeUpload(task.urlFile)
                    showToastMessage(R.string.message_change_upload)
                    Timber.d("Upload image complete")
                    killServices()
                }
                is StorageTaskResult.InProgress -> {
                    // ? when the upload is in progress, update the notification
                    notifyHelper.updateNotifyProgressUpload(task.percent.toInt())
                    Timber.d("Percent upload ${task.percent}")
                }
                else -> Unit
            }
        }
    }

    private fun killServices() {
        stopForeground(true)
        stopSelf()
    }

    private fun actionStopCommand() {
        // * notify to user that operation has been stop
        showToastMessage(R.string.message_cancel_upload)
        jobUploadTask?.cancel()
        killServices()
    }

}