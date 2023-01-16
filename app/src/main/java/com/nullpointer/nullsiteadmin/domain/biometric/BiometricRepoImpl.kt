package com.nullpointer.nullsiteadmin.domain.biometric

import com.nullpointer.nullsiteadmin.actions.BiometricResult.*
import com.nullpointer.nullsiteadmin.actions.BiometricState
import com.nullpointer.nullsiteadmin.data.local.biometric.BiometricDataSource
import com.nullpointer.nullsiteadmin.data.local.settings.SettingsDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transform

class BiometricRepoImpl(
    private val settingsDataSource: SettingsDataSource,
    private val biometricDataSource: BiometricDataSource,
) : BiometricRepository {

    private val _biometricState = MutableStateFlow(BiometricState.Locked)
    override val biometricState: Flow<BiometricState> = _biometricState

    override val isBiometricEnabled: Flow<Boolean> = settingsDataSource.isBiometricEnabled()
    override val timeOutLocked: Flow<Long> = settingsDataSource.timeOutLocked().transform { value ->
        var restNow = value - System.currentTimeMillis()
        while (restNow > 0) {
            delay(50)
            restNow = value - System.currentTimeMillis()
            emit(restNow / 1000)
            _biometricState.value = BiometricState.LockedTimeOut
        }
        _biometricState.value = BiometricState.Locked
        emit(0)
    }


    override fun checkBiometricSupport(): Boolean =
        biometricDataSource.checkBiometricSupport()

    override suspend fun changeIsBiometricEnabled(newValue: Boolean) {
        if (newValue) {
            val result = biometricDataSource.enableFingerBiometric()
            if (result == PASSED) settingsDataSource.changeBiometricEnabled(true)
        } else {
            settingsDataSource.changeBiometricEnabled(false)
        }
    }

    override suspend fun launchBiometric(): Boolean {
        return when (biometricDataSource.launchFingerBiometric()) {
            PASSED -> true
            DISABLE_ALWAYS -> {
                _biometricState.value = BiometricState.DisabledTimeOut
                false
            }
            LOCKED_TIME_OUT -> {
                settingsDataSource.changeTimeOutLocked(System.currentTimeMillis() + 30 * 1000)
                false
            }
        }
    }

    override suspend fun changeTimeOut(newValue: Long) {
        settingsDataSource.changeTimeOutLocked(newValue)
    }
}