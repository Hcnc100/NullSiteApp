package com.nullpointer.nullsiteadmin.ui.screens.settings.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.utils.ExceptionManager
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val biometricRepository: BiometricRepository
) : ViewModel() {

    private val _messageErrorSettings = Channel<Int>()
    val messageErrorSettings = _messageErrorSettings.receiveAsFlow()

    val isBiometricAvailable by mutableStateOf(biometricRepository.checkBiometricSupport())

    val isBiometricEnabled = biometricRepository
        .isBiometricEnabled
        .catch {
            Timber.e("Error get is Biometric enabled $it")
            emit(false)
        }
        .flowOn(Dispatchers.IO).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            false
        )


    fun changeBiometricEnabled(enabled: Boolean) = launchSafeIO(
        blockIO = {
            if (isBiometricAvailable) {
                biometricRepository.changeBiometricEnable(enabled)
            } else {
                _messageErrorSettings.trySend(R.string.biometric_no_avariable)
            }
        },
        blockException = {
            ExceptionManager.sendMessageErrorToException(
                exception = it,
                channel = _messageErrorSettings,
                messageResource = R.string.error_enable_biometric,
                debugMessage = "Error to change biometric enabled"
            )
        }
    )

}