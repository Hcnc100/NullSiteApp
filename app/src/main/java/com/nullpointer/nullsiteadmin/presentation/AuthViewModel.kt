package com.nullpointer.nullsiteadmin.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.actions.BiometricState
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.ExceptionManager
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepository
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepository
import com.nullpointer.nullsiteadmin.domain.deleter.DeleterInfoRepository
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepository
import com.nullpointer.nullsiteadmin.models.UserCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val infoUserRepository: InfoUserRepository,
    private val biometricRepository: BiometricRepository,
    private val deleterInfoRepository: DeleterInfoRepository
) : ViewModel() {

    val stateLocked = biometricRepository.biometricState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        BiometricState.Locked
    )
    var isAuthBiometricPassed by mutableStateOf<Boolean?>(null)
        private set

    private val _messageErrorAuth = Channel<Int>()
    val messageErrorAuth = _messageErrorAuth.receiveAsFlow()


    val timeOutLocked = biometricRepository.timeOutLocked.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Long.MAX_VALUE
    )

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

    // * this is no savable because when the activity finish process (for example no memory)
    // * the function auth finish and restore state of "isAuthenticating"
    var isAuthenticating by mutableStateOf(false)
        private set

    val isBiometricAvailable by mutableStateOf(biometricRepository.checkBiometricSupport())

    init {
        verifyTokenMessaging()
        initVerifyBiometrics()
    }

    fun authWithEmailAndPassword(
        userCredentials: UserCredentials
    ) = launchSafeIO(
        blockBefore = { isAuthenticating = true },
        blockAfter = { isAuthenticating = false },
        blockIO = {
            authRepository.authUserWithEmailAndPassword(
                email = userCredentials.email,
                password = userCredentials.password
            )
            infoUserRepository.requestLastPersonalInfo(false)
        },
        blockException = {
            ExceptionManager.sendMessageErrorToException(
                exception = it,
                message = "Error auth",
                channel = _messageErrorAuth
            )
        }
    )

    fun logOut() = launchSafeIO {
        authRepository.logout()
        deleterInfoRepository.deleterAllInformation()
    }

    private fun verifyTokenMessaging() = launchSafeIO(
        blockIO = {
            val isTokenUpdated = authRepository.verifyTokenMessaging()
            if (isTokenUpdated) Timber.d("Se actualizo el token")
        },
        blockException = {
            ExceptionManager.getMessageForException(it, "Error update token user")
        }
    )

    fun changeBiometricEnabled(enabled: Boolean) = launchSafeIO {
        if (isBiometricAvailable) {
            biometricRepository.changeIsBiometricEnabled(enabled)
        } else {
            _messageErrorAuth.trySend(R.string.biometric_no_avariable)
        }


    }

    private fun initVerifyBiometrics() = launchSafeIO {
        when {
            !biometricRepository.checkBiometricSupport() -> true
            else -> !biometricRepository.isBiometricEnabled.first()
        }.let {
            withContext(Dispatchers.Main) {
                isAuthBiometricPassed = it
            }
        }

    }

    fun launchBiometric() = launchSafeIO {
        val timeOut = biometricRepository.timeOutLocked.first()
        val supportBiometric = biometricRepository.checkBiometricSupport()
        when {
            !supportBiometric -> true
            timeOut == 0L -> biometricRepository.launchBiometric()
            else -> null
        }?.let {
            withContext(Dispatchers.Main) {
                isAuthBiometricPassed = it
            }
        }
    }
}