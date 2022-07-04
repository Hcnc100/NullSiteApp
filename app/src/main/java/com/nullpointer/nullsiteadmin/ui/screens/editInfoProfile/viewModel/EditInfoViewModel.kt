package com.nullpointer.nullsiteadmin.ui.screens.editInfoProfile.viewModel

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.SavableProperty
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import com.nullpointer.nullsiteadmin.models.PropertySavableImg
import com.nullpointer.nullsiteadmin.models.PropertySavableString
import com.nullpointer.nullsiteadmin.services.imageProfile.UploadImageServicesControl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import javax.inject.Inject

@HiltViewModel
class EditInfoViewModel @Inject constructor(
    state: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val MAX_LENGTH_NAME = 50
        private const val MAX_LENGTH_DESCRIPTION = 250
        private const val MAX_LENGTH_PROFESSION = 50
        private const val KEY_PROJECT_SAVED = "KEY_PROJECT_SAVED"
    }

    private var personalInfo: PersonalInfo? by SavableProperty(state, KEY_PROJECT_SAVED, null)
    private val _messageError = Channel<Int>()
    val messageError = _messageError.consumeAsFlow()


    val name = PropertySavableString(
        state = state,
        maxLength = MAX_LENGTH_NAME,
        emptyError = R.string.error_name_admin_empty,
        lengthError = R.string.error_name_admin_length,
        label = R.string.label_name_admin,
        hint = R.string.hint_name_admin
    )
    val profession = PropertySavableString(
        state = state,
        maxLength = MAX_LENGTH_PROFESSION,
        emptyError = R.string.error_profess_admin_empty,
        lengthError = R.string.error_profess_admin_length,
        label = R.string.label_profession_admin,
        hint = R.string.hint_profession_admin
    )
    val description = PropertySavableString(
        state = state,
        maxLength = MAX_LENGTH_DESCRIPTION,
        emptyError = R.string.error_description_admin_empty,
        lengthError = R.string.error_description_admin_length,
        label = R.string.label_description_admin,
        hint = R.string.hint_description_admin
    )

    val imageProfile = PropertySavableImg(
        state = state,
        scope = viewModelScope,
        actionSendError = _messageError::trySend
    )

    val isDataValid: Boolean
        get() = !name.hasError && !profession.hasError && !description.hasError

     val hasAnyChange: Boolean
        get() = name.value != personalInfo?.name ||
                profession.value != personalInfo?.profession ||
                description.value != personalInfo?.description || imageProfile.value != personalInfo?.urlImg?.toUri()


    fun initInfoProfile(personalInfo: PersonalInfo) {
        this.personalInfo = personalInfo
        personalInfo.let {
            name.changeValue(it.name)
            profession.changeValue(it.profession)
            description.changeValue(it.description)
            imageProfile.initValue(personalInfo.urlImg.toUri())
        }
    }

    fun getUpdatedPersonalInfo(context: Context):PersonalInfo?{
         return if (hasAnyChange) {
             val finishInfo=personalInfo!!.copy(
                 name = name.value,
                 description = description.value,
                 profession = profession.value
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


    fun updateImg(uri: Uri, context: Context) {
        imageProfile.changeValue(uri, context)
    }

    override fun onCleared() {
        super.onCleared()
        name.clearValue()
        description.clearValue()
        profession.clearValue()
        imageProfile.clearValue()
    }
}