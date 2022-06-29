package com.nullpointer.nullsiteadmin.ui.screens.editInfoProfile.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.SavableProperty
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import com.nullpointer.nullsiteadmin.models.PropertySavableString
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
    }

    private var personalInfo: PersonalInfo? by SavableProperty(state, "", null)

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

    val isDataValid: Boolean
        get() = !name.hasError && !profession.hasError && !description.hasError

    private val hasAnyChange: Boolean
        get() = name.value != personalInfo?.name ||
                profession.value != personalInfo?.profession ||
                description.value != personalInfo?.description

    private val _messageError = Channel<Int>()
    val messageError = _messageError.consumeAsFlow()

    fun initInfoProfile(personalInfo: PersonalInfo) {
        this.personalInfo = personalInfo
        personalInfo.let {
            name.changeValue(it.name)
            profession.changeValue(it.profession)
            description.changeValue(it.description)
        }
    }

    fun getUpdatedPersonalInfo(): PersonalInfo? {
        return if (hasAnyChange) {
            personalInfo?.copy(
                name = name.value,
                description = description.value,
                profession = profession.value
            )
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
    }
}