package com.nullpointer.nullsiteadmin.services.imageProfile

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.StorageTaskResult
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.core.utils.showToastMessage
import com.nullpointer.nullsiteadmin.data.local.services.ServicesManager.Companion.KEY_INFO_PROFILE
import com.nullpointer.nullsiteadmin.data.local.services.ServicesManager.Companion.KEY_URI_PROFILE
import com.nullpointer.nullsiteadmin.data.local.services.ServicesManager.Companion.START_COMMAND
import com.nullpointer.nullsiteadmin.data.local.services.ServicesManager.Companion.STOP_COMMAND
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepository
import com.nullpointer.nullsiteadmin.domain.storage.ImageRepository
import com.nullpointer.nullsiteadmin.models.data.PersonalInfoData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class UploadImageServices : LifecycleService() {

    @Inject
    lateinit var imageRepositoryImpl: ImageRepository

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
        jobUploadTask?.cancel()
        jobUploadTask = lifecycleScope.launchSafeIO(
            blockException = {
                Timber.e("Error to upload info profile $it")
                showToastMessage(R.string.error_upload_info)
            },
            blockAfter = {
                showToastMessage(R.string.message_change_upload)
                killServices()
            },
            blockBefore = {
                showToastMessage(R.string.message_init_upload)
            },
            blockIO = {
                val uriInfo: Uri = intent.getParcelableExtra(KEY_URI_PROFILE)!!
                val personalEncode = intent.getStringExtra(KEY_INFO_PROFILE)!!
                val personalInfoData: PersonalInfoData = Json.decodeFromString(personalEncode)

                notifyHelper.startServicesForeground(this@UploadImageServices)

//                val newUrlImg = startUploadImage(personalInfoData.idPersonal, uriInfo)!!
//                val personUpdated = personalInfoData.copy(urlImg = newUrlImg)

//                infoUserRepository.updatePersonalInfo(personUpdated)
            }
        )
    }

    private suspend fun startUploadImage(
        idUser: String,
        uriImage: Uri,
    ): String? {
        var urlImage: String? = null

        imageRepositoryImpl.uploadImageProfile(uriImage, idUser).catch { exception ->
            Timber.e("Error upload img flow $exception")
        }.flowOn(Dispatchers.IO).collect { task ->
            when (task) {
                is StorageTaskResult.Complete.Failed -> {
                    Timber.e("Error upload img flow ${task.error}")
                }
                is StorageTaskResult.Complete.Success -> {
                    notifyHelper.updateNotifyFinishUpdate()
                    urlImage = task.urlFile
                    Timber.d("Upload image complete")
                }
                is StorageTaskResult.InProgress -> {
                    notifyHelper.updateNotifyProgressUpload(task.percent.toInt())
                    Timber.d("Percent upload ${task.percent}")
                }
                else -> Unit
            }
        }

        return urlImage
    }

    private fun killServices() {
        stopForeground(true)
        stopSelf()
    }

    private fun actionStopCommand() {
        // * notify to user that operation has been stop
        jobUploadTask?.cancel()
        killServices()
    }

}