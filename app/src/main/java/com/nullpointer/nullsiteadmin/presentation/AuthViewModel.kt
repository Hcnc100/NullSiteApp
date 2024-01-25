package com.nullpointer.nullsiteadmin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepository
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val biometricRepository: BiometricRepository,
) : ViewModel() {

    private val _messageErrorAuth = Channel<Int>()
    val messageErrorAuth = _messageErrorAuth.receiveAsFlow()

    val isAuthBiometricPassed = biometricRepository.isAuthBiometricPassed.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )


    val isUserAuth = authRepository.isUserAuth.transform<Boolean, Resource<Boolean>> { isAuth ->
        emit(Resource.Success(isAuth))
    }.flowOn(
        Dispatchers.IO
    ).catch {
        Timber.e("Error to load info auth $it")
        _messageErrorAuth.trySend(R.string.error_unkown)
        emit(Resource.Failure)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )


    fun initVerifyBiometrics() = launchSafeIO {
        biometricRepository.initVerifyBiometrics()
    }

    fun blockBiometric() {
        biometricRepository.blockBiometric()
    }


}