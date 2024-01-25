package com.nullpointer.nullsiteadmin.ui.screens.lock.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepository
import com.nullpointer.nullsiteadmin.models.biometric.data.BiometricLockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LockScreenViewModel @Inject constructor(
    private val biometricRepository: BiometricRepository
) : ViewModel() {


    private val _messageErrorBiometric = Channel<Int>()
    val messageErrorBiometric = _messageErrorBiometric.receiveAsFlow()

    val biometricLockData: StateFlow<Resource<BiometricLockData>> = combine(
        biometricRepository.timeOutLocked,
        biometricRepository.biometricLockState
    ) { timeOutLocked, biometricState ->
        Resource.Success(
            BiometricLockData(
                timeOutLock = timeOutLocked,
                biometricLockState = biometricState
            )
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )


    fun launchBiometric() = launchSafeIO(
        blockIO = {
            withContext(Dispatchers.Main) {
                biometricRepository.unlockByBiometric()
            }
        },
        blockException = {
            Timber.e("Error launch biometric $it")
            _messageErrorBiometric.send(R.string.biometric_error_launch)
        }
    )

}