package com.nullpointer.nullsiteadmin.domain.biometric

import com.nullpointer.nullsiteadmin.actions.BiometricLockState
import kotlinx.coroutines.flow.Flow

interface BiometricRepository {
    val isBiometricEnabled: Flow<Boolean>
    val timeOutLocked: Flow<Long>
    val biometricState: Flow<BiometricLockState>
    fun checkBiometricSupport(): Boolean
    suspend fun unlockByBiometric(): Boolean
    suspend fun changeBiometricEnable(newValue: Boolean)
    suspend fun changeTimeOutLocked(newValue: Long)
}