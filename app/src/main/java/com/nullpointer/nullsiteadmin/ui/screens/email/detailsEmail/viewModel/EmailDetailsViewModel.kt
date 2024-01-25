package com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.utils.ExceptionManager
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepository
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepository
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailDetailsViewModel @Inject constructor(
    private val emailsRepository: EmailsRepository,
    private val biometricRepository: BiometricRepository,
) : ViewModel() {

    val isAuthBiometricPassed = biometricRepository.isAuthBiometricPassed.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )


    private val _errorEmailDetails = Channel<Int>()
    val errorEmailDetails = _errorEmailDetails.receiveAsFlow()

    fun markAsOpen(email: EmailData) = launchSafeIO(
        isEnabled = !email.isOpen,
        blockIO = { emailsRepository.markAsOpen(email) },
        blockException = { Timber.e("Error mark as open $email : $it") }
    )

    fun deleterEmail(
        idEmail: String
    ) = launchSafeIO(
        blockIO = {
            emailsRepository.deleterEmail(idEmail)
            delay(300)
            _errorEmailDetails.trySend(R.string.message_deleter_email_success)
        },
        blockException = {
            delay(300)
            ExceptionManager.sendMessageErrorToException(
                exception = it,
                channel = _errorEmailDetails,
                message = "Error deleter email $idEmail"
            )
        }
    )

}