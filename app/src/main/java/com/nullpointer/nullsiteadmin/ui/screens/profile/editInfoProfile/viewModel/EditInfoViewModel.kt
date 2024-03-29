package com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableImg
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.core.utils.ExceptionManager
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.image.ImageRepository
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepository
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.wrapper.UpdateInfoProfileWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditInfoViewModel @Inject constructor(
    state: SavedStateHandle,
    private val infoUserRepository: InfoUserRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    companion object {
        private const val MAX_LENGTH_NAME = 50
        private const val MAX_LENGTH_DESCRIPTION = 250
        private const val MAX_LENGTH_PROFESSION = 50
        private const val TAG_IMG_ADMIN = "TAG_IMG_ADMIN"
        private const val TAG_NAME_ADMIN = "TAG_NAME_ADMIN"
        private const val TAG_PROFESSION_ADMIN = "TAG_PROFESSION_ADMIN"
        private const val TAG_DESCRIPTION_ADMIN = "TAG_DESCRIPTION_ADMIN"
    }

    private val _messageError = Channel<Int>()
    val messageError = _messageError.receiveAsFlow()


    val name = PropertySavableString(
        savedState = state,
        maxLength = MAX_LENGTH_NAME,
        emptyError = R.string.error_name_admin_empty,
        lengthError = R.string.error_name_admin_length,
        label = R.string.label_name_admin,
        hint = R.string.hint_name_admin,
        tagSavable = TAG_NAME_ADMIN
    )
    val profession = PropertySavableString(
        savedState = state,
        maxLength = MAX_LENGTH_PROFESSION,
        emptyError = R.string.error_profess_admin_empty,
        lengthError = R.string.error_profess_admin_length,
        label = R.string.label_profession_admin,
        hint = R.string.hint_profession_admin,
        tagSavable = TAG_PROFESSION_ADMIN
    )
    val description = PropertySavableString(
        savedState = state,
        maxLength = MAX_LENGTH_DESCRIPTION,
        emptyError = R.string.error_description_admin_empty,
        lengthError = R.string.error_description_admin_length,
        label = R.string.label_description_admin,
        hint = R.string.hint_description_admin,
        tagSavable = TAG_DESCRIPTION_ADMIN
    )

    val imageProfile = PropertySavableImg(
        state = state,
        tagSavable = TAG_IMG_ADMIN
    )

    val isDataValid: Boolean
        get() = !name.hasError && !profession.hasError && !description.hasError

    private val hasAnyChange: Boolean
        get() = name.hasChanged ||
                profession.hasChanged ||
                description.hasChanged || imageProfile.hasChanged

    var isUpdatedData by mutableStateOf(false)
        private set


    fun changeImageSelected(
        currentImage: Uri,
        actionSuccessCompress: (Uri) -> Unit
    ) = launchSafeIO(
        blockBefore = { imageProfile.changeImageLoading(true) },
        blockAfter = { imageProfile.changeImageLoading(false) },
        blockIO = {
            val imageCompress = imageRepository.compressImg(currentImage)
            withContext(Dispatchers.Main) {
                actionSuccessCompress(imageCompress)
            }
        },
        blockException = {
            _messageError.trySend(R.string.error_compress_img)
        }
    )


    fun initInfoProfile(personalInfoData: PersonalInfoData?) {
        personalInfoData?.let {
            name.setDefaultValue(it.name)
            profession.setDefaultValue(it.profession)
            description.setDefaultValue(it.description)
            imageProfile.setDefaultValue(personalInfoData.urlImg.toUri())
        }
    }

    fun validateInfoProfile(): UpdateInfoProfileWrapper? {
       if (!isDataValid) {
            _messageError.trySend(R.string.error_invalid_data)
            return null
        }
        if (!hasAnyChange) {
            _messageError.trySend(R.string.error_no_data_change)
            return null
        }
        return UpdateInfoProfileWrapper(
            name = name.getValueOnlyHasChanged(),
            profession = profession.getValueOnlyHasChanged(),
            description = description.getValueOnlyHasChanged(),
            uriFileImgProfile = imageProfile.getValueOnlyHasChanged(),
        )
    }

    fun updatePersonalInfo(
        actionComplete: () -> Unit,
        updateInfoProfileWrapper: UpdateInfoProfileWrapper
    ) = launchSafeIO(
        blockBefore = { isUpdatedData = true },
        blockAfter = { isUpdatedData = false },
        blockIO = {
            infoUserRepository.updatePersonalInfo(
                updateInfoProfileWrapper
            )
            delay(1000)
            withContext(Dispatchers.Main) {
                actionComplete()
            }
        },
        blockException = {
            delay(300)
            ExceptionManager.sendMessageErrorToException(
                exception = it,
                message = "Error to update info personal $it",
                channel = _messageError
            )
        }
    )

}