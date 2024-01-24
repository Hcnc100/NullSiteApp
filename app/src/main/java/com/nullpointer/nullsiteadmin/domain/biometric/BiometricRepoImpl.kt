package com.nullpointer.nullsiteadmin.domain.biometric

import com.nullpointer.nullsiteadmin.actions.BiometricLockState
import com.nullpointer.nullsiteadmin.actions.BiometricResultState.DISABLE
import com.nullpointer.nullsiteadmin.actions.BiometricResultState.NOT_SUPPORTED
import com.nullpointer.nullsiteadmin.actions.BiometricResultState.PASSED
import com.nullpointer.nullsiteadmin.actions.BiometricResultState.TEMPORARILY_LOCKED
import com.nullpointer.nullsiteadmin.datasource.biometric.local.BiometricDataSource
import com.nullpointer.nullsiteadmin.datasource.settings.local.SettingsLocalDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

class BiometricRepoImpl(
    private val settingsLocalDataSource: SettingsLocalDataSource,
    private val biometricDataSource: BiometricDataSource,
) : BiometricRepository {

    private val _biometricState = MutableStateFlow(BiometricLockState.LOCK)
    override val biometricState: Flow<BiometricLockState> = _biometricState

    override val isBiometricEnabled: Flow<Boolean> = settingsLocalDataSource.getSettingsData().map {
        it?.isBiometricEnabled ?: false
    }

    override val timeOutLocked: Flow<Long> = settingsLocalDataSource.getSettingsData().map {
        it?.timeOutLock ?: 0
    }.transform { value ->
        var restNow = value - System.currentTimeMillis()
        while (restNow > 0) {
            delay(150)
            restNow = value - System.currentTimeMillis()
            emit(restNow / 1000)
            _biometricState.value = BiometricLockState.LOCKED_BY_TIME_OUT
        }
        _biometricState.value = BiometricLockState.LOCK
        emit(0)
    }.distinctUntilChanged()


    override fun checkBiometricSupport(): Boolean =
        biometricDataSource.checkBiometricSupport()

    override suspend fun changeBiometricEnable(newValue: Boolean) {
        if (newValue) {
            // * only enable if biometric is supported and passed the authentication
            val result = biometricDataSource.enableFingerBiometric()
            if (result == PASSED) {
                settingsLocalDataSource.changeBiometricEnabled(true)
            }
        } else {
            settingsLocalDataSource.changeBiometricEnabled(false)
        }
    }

    override suspend fun unlockByBiometric(): Boolean {
        return when (biometricDataSource.unlockByFingerBiometric()) {
            PASSED -> true
            DISABLE -> {
                _biometricState.value = BiometricLockState.LOCKED_BY_MANY_INTENTS
                false
            }

            TEMPORARILY_LOCKED -> {
                settingsLocalDataSource.changeTimeOutLocked(System.currentTimeMillis() + 30 * 1000)
                false
            }

            NOT_SUPPORTED -> {
                _biometricState.value = BiometricLockState.UNAVAILABLE
                false
            }
        }
    }

    override suspend fun changeTimeOutLocked(newValue: Long) =
        settingsLocalDataSource.changeTimeOutLocked(newValue)
}