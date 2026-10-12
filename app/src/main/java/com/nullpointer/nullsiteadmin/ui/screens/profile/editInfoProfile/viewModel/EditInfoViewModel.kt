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
import com.nullpointer.nullsiteadmin.core.utils.FormLengthLimits
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class EditInfoViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val infoUserRepository: InfoUserRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    companion object {
        private const val MAX_LENGTH_NAME = FormLengthLimits.PROFILE_NAME
        private const val MAX_LENGTH_DESCRIPTION = FormLengthLimits.PROFILE_DESCRIPTION
        private const val MAX_LENGTH_PROFESSION = FormLengthLimits.PROFILE_PROFESSION
        private const val TAG_BASELINE = "PROFILE_EDIT_BASELINE"
        private const val TAG_IMG_ADMIN = "TAG_IMG_ADMIN"
        private const val TAG_NAME_ADMIN = "TAG_NAME_ADMIN"
        private const val TAG_PROFESSION_ADMIN = "TAG_PROFESSION_ADMIN"
        private const val TAG_DESCRIPTION_ADMIN = "TAG_DESCRIPTION_ADMIN"
    }

    private var initialized = false
    private var baseline: PersonalInfoData? = null
    val profileContent = ProfileContentEditorState(state)

    private val _messageError = Channel<Int>()
    val messageError = _messageError.receiveAsFlow()


    val name = PropertySavableString(
        savedState = state,
        maxLength = MAX_LENGTH_NAME,
        emptyError = R.string.error_name_admin_empty,
        lengthError = R.string.error_name_admin_length,
        label = R.string.label_name_admin,
        hint = R.string.hint_name_admin,
        tagSavable = TAG_NAME_ADMIN,
        rejectBlank = true,
    )
    val profession = PropertySavableString(
        savedState = state,
        maxLength = MAX_LENGTH_PROFESSION,
        emptyError = R.string.error_profess_admin_empty,
        lengthError = R.string.error_profess_admin_length,
        label = R.string.label_profession_admin,
        hint = R.string.hint_profession_admin,
        tagSavable = TAG_PROFESSION_ADMIN,
        rejectBlank = true,
    )
    val description = PropertySavableString(
        savedState = state,
        maxLength = MAX_LENGTH_DESCRIPTION,
        emptyError = R.string.error_description_admin_empty,
        lengthError = R.string.error_description_admin_length,
        label = R.string.label_description_admin,
        hint = R.string.hint_description_admin,
        tagSavable = TAG_DESCRIPTION_ADMIN,
        rejectBlank = true,
    )

    val imageProfile = PropertySavableImg(
        state = state,
        tagSavable = TAG_IMG_ADMIN
    )

    val isDataValid: Boolean
        get() = !name.hasError && !profession.hasError && !description.hasError && profileContent.isValid

    private val hasAnyChange: Boolean
        get() = name.hasChanged ||
                profession.hasChanged ||
                description.hasChanged || imageProfile.hasChanged || profileContent.hasChanged(baseline)

    val hasUnsavedChanges: Boolean get() = initialized && hasAnyChange

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
        if (initialized) return
        val saved: String? = state[TAG_BASELINE]
        baseline = if (saved != null) Json.decodeFromString<PersonalInfoData?>(saved) else personalInfoData
        state[TAG_BASELINE] = Json.encodeToString(baseline)
        profileContent.initialize(baseline)
        baseline?.let {
            name.setDefaultValue(it.name, preserveCurrentValue = saved != null)
            profession.setDefaultValue(it.profession, preserveCurrentValue = saved != null)
            description.setDefaultValue(it.description, preserveCurrentValue = saved != null)
            imageProfile.setDefaultValue(it.urlImg.toUri(), preserveCurrentValue = saved != null)
        }
        initialized = true
    }

    fun validateInfoProfile(): UpdateInfoProfileWrapper? {
        if (!initialized) return null
        name.reValueField()
        profession.reValueField()
        description.reValueField()
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
            baseline = baseline,
            socialLinks = profileContent.content.socialLinks.takeIf { profileContent.linksChanged(baseline) },
            translations = profileContent.content.translations.takeIf { profileContent.translationsChanged(baseline) },
        )
    }

    fun updatePersonalInfo(
        actionComplete: () -> Unit,
        updateInfoProfileWrapper: UpdateInfoProfileWrapper
    ) = launchSafeIO(
        isEnabled = !isUpdatedData,
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
                debugMessage = "Error to update info personal",
                channel = _messageError,
                messageResource = R.string.error_update_profile_info
            )
        }
    )

}
