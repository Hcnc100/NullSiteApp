package com.nullpointer.nullsiteadmin.data.local.biometric

import com.nullpointer.nullsiteadmin.actions.BiometricResult

interface BiometricDataSource {
    fun checkBiometricSupport(): Boolean
    suspend fun enableFingerBiometric(): BiometricResult
    suspend fun launchFingerBiometric(): BiometricResult
}