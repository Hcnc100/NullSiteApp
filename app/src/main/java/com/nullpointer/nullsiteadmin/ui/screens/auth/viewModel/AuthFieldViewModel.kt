package com.nullpointer.nullsiteadmin.ui.screens.auth.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.PropertySavableString
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthFieldViewModel @Inject constructor(
    state: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val MAX_LENGTH_EMAIL = 50
        private const val MAX_LENGTH_PASSWORD = 50
    }

    val emailAdmin = PropertySavableString(
        state = state,
        label = R.string.label_email,
        hint = R.string.hint_email,
        emptyError = R.string.error_empty_email,
        lengthError = R.string.error_length_email,
        maxLength = MAX_LENGTH_EMAIL
    )

    val passwordAdmin = PropertySavableString(
        state = state,
        label = R.string.label_password,
        hint = R.string.hint_password,
        emptyError = R.string.error_empty_passwod,
        lengthError = R.string.error_length_password,
        maxLength = MAX_LENGTH_PASSWORD
    )

    private val isDataValid get() = !emailAdmin.hasError && !passwordAdmin.hasError

    fun getDataAuth(): Pair<String, String>? {
        return if (isDataValid) {
            Pair(emailAdmin.value, passwordAdmin.value)
        } else {
            null
        }
    }
}