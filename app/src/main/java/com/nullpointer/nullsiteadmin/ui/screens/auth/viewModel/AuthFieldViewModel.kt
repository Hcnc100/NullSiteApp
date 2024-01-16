package com.nullpointer.nullsiteadmin.ui.screens.auth.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.models.wrapper.CredentialsWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class AuthFieldViewModel @Inject constructor(
    state: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val MAX_LENGTH_EMAIL = 50
        private const val MAX_LENGTH_PASSWORD = 50
        private const val TAG_EMAIL_SIGN_IN = "TAG_EMAIL_SIGN_IN"
        private const val TAG_PASSWORD_SIGN_IN = "TAG_PASSWORD_SIGN_IN"
    }

    val emailAdmin = PropertySavableString(
        savedState = state,
        label = R.string.label_email,
        hint = R.string.hint_email,
        emptyError = R.string.error_empty_email,
        lengthError = R.string.error_length_email,
        maxLength = MAX_LENGTH_EMAIL,
        tagSavable = TAG_EMAIL_SIGN_IN
    )

    val passwordAdmin = PropertySavableString(
        savedState = state,
        label = R.string.label_password,
        hint = R.string.hint_password,
        emptyError = R.string.error_empty_passwod,
        lengthError = R.string.error_length_password,
        maxLength = MAX_LENGTH_PASSWORD,
        tagSavable = TAG_PASSWORD_SIGN_IN
    )

    private val isDataValid get() = !emailAdmin.hasError && !passwordAdmin.hasError

    private val _messageCredentials = Channel<Int>()
    val messageCredentials get() = _messageCredentials.receiveAsFlow()

    fun getDataAuth(): CredentialsWrapper? {
        emailAdmin.reValueField()
        passwordAdmin.reValueField()
        return if (isDataValid) {
            CredentialsWrapper(
                email = emailAdmin.currentValue,
                password = passwordAdmin.currentValue
            )
        } else {
            _messageCredentials.trySend(R.string.error_data_invalid)
            null
        }
    }
}