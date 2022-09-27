package com.nullpointer.nullsiteadmin.domain.biometric

import kotlinx.coroutines.flow.Flow

interface BiometricRepository {
    val isBiometricEnabled: Flow<Boolean>
    fun checkBiometricSupport(): Boolean
    fun launchBiometric(callbackResult: (Boolean) -> Unit, callbackCancel: () -> Unit)
    suspend fun changeIsBiometricEnabled(newValue: Boolean)
}