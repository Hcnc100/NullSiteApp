package com.nullpointer.nullsiteadmin.domain.biometric

import com.nullpointer.nullsiteadmin.actions.BiometricResult.*
import com.nullpointer.nullsiteadmin.actions.BiometricState
import com.nullpointer.nullsiteadmin.datasource.biometric.local.BiometricDataSource
import com.nullpointer.nullsiteadmin.datasource.settings.local.SettingsLocalDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

class BiometricRepoImpl(
    private val settingsLocalDataSource: SettingsLocalDataSource,
    private val biometricDataSource: BiometricDataSource,
) : BiometricRepository {

    private val _biometricState = MutableStateFlow(BiometricState.Locked)
    override val biometricState: Flow<BiometricState> = _biometricState

    override val isBiometricEnabled: Flow<Boolean> = settingsLocalDataSource.getSettingsData().map {
        it?.isBiometricEnabled ?: false
    }

    override val timeOutLocked: Flow<Long> = settingsLocalDataSource.getSettingsData().map {
        it?.timeOutLock ?: 0
    }.transform { value ->
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
            if (result == PASSED) settingsLocalDataSource.changeBiometricEnabled(true)
        } else {
            settingsLocalDataSource.changeBiometricEnabled(false)
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
                settingsLocalDataSource.changeTimeOutLocked(System.currentTimeMillis() + 30 * 1000)
                false
            }
        }
    }

    override suspend fun changeTimeOut(newValue: Long) {
        settingsLocalDataSource.changeTimeOutLocked(newValue)
    }
}