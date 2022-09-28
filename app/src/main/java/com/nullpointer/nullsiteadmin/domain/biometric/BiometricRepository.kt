package com.nullpointer.nullsiteadmin.domain.biometric

import com.nullpointer.nullsiteadmin.actions.BiometricState
import kotlinx.coroutines.flow.Flow

interface BiometricRepository {
    val isBiometricEnabled: Flow<Boolean>
    val timeOutLocked: Flow<Long>
    val biometricState: Flow<BiometricState>
    fun checkBiometricSupport(): Boolean
    suspend fun launchBiometric(callbackSuccess: () -> Unit)
    suspend fun changeIsBiometricEnabled(newValue: Boolean)
    suspend fun changeTimeOut(newValue: Long)
}