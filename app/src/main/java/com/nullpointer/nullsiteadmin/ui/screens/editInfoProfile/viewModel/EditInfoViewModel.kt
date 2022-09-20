package com.nullpointer.nullsiteadmin.ui.screens.editInfoProfile.viewModel

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableImg
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.core.delagetes.SavableProperty
import com.nullpointer.nullsiteadmin.domain.compress.CompressImgRepository
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import com.nullpointer.nullsiteadmin.services.imageProfile.UploadImageServicesControl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class EditInfoViewModel @Inject constructor(
    private val compressImgRepository: CompressImgRepository,
    state: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val MAX_LENGTH_NAME = 50
        private const val MAX_LENGTH_DESCRIPTION = 250
        private const val MAX_LENGTH_PROFESSION = 50
        private const val KEY_PROJECT_SAVED = "KEY_PROJECT_SAVED"

        private const val TAG_IMG_ADMIN = "TAG_IMG_ADMIN"
        private const val TAG_NAME_ADMIN = "TAG_NAME_ADMIN"
        private const val TAG_PROFESSION_ADMIN = "TAG_PROFESSION_ADMIN"
        private const val TAG_DESCRIPTION_ADMIN = "TAG_DESCRIPTION_ADMIN"
    }

    private var personalInfo: PersonalInfo? by SavableProperty(state, KEY_PROJECT_SAVED, null)
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
        scope = viewModelScope,
        tagSavable = TAG_IMG_ADMIN,
        actionSendErrorCompress = { _messageError.trySend(R.string.error_compress_img) },
        actionCompress = compressImgRepository::compressImg
    )

    val isDataValid: Boolean
        get() = !name.hasError && !profession.hasError && !description.hasError

    val hasAnyChange: Boolean
        get() = name.currentValue != personalInfo?.name ||
                profession.currentValue != personalInfo?.profession ||
                description.currentValue != personalInfo?.description || imageProfile.value != personalInfo?.urlImg?.toUri()


    fun initInfoProfile(personalInfo: PersonalInfo) {
        this.personalInfo = personalInfo
        personalInfo.let {
            name.changeValue(newValue = it.name, isInit = true)
            profession.changeValue(newValue = it.profession, isInit = true)
            description.changeValue(newValue = it.description, isInit = true)
            imageProfile.changeValue(newValue = personalInfo.urlImg.toUri(), isInit = true)
        }
    }

    fun getUpdatedPersonalInfo(context: Context):PersonalInfo?{
         return if (hasAnyChange) {
             val finishInfo=personalInfo!!.copy(
                 name = name.currentValue,
                 profession = profession.currentValue,
                 description = description.currentValue
             )
             if(imageProfile.value!= personalInfo!!.urlImg.toUri()){
                 UploadImageServicesControl.init(context,finishInfo,imageProfile.value)
                 null
             }else{
                 finishInfo
             }
        } else {
            _messageError.trySend(R.string.error_no_data_change)
            null
        }
    }


    override fun onCleared() {
        super.onCleared()
        name.clearValue()
        description.clearValue()
        profession.clearValue()
        imageProfile.clearValue()
    }
}