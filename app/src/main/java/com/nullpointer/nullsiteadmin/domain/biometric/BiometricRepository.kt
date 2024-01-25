package com.nullpointer.nullsiteadmin.domain.biometric

import com.nullpointer.nullsiteadmin.actions.BiometricLockState
import kotlinx.coroutines.flow.Flow

interface BiometricRepository {
    val isAuthBiometricPassed: Flow<Boolean?>
    val isBiometricEnabled: Flow<Boolean>
    val timeOutLocked: Flow<Long>
    val biometricLockState: Flow<BiometricLockState>
    fun checkBiometricSupport(): Boolean
    suspend fun unlockByBiometric()
    suspend fun changeBiometricEnable(newValue: Boolean)
    suspend fun changeTimeOutLocked(newValue: Long)

    suspend fun initVerifyBiometrics()

    fun blockBiometric()

}