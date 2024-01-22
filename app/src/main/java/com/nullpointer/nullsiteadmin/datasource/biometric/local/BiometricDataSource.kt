package com.nullpointer.nullsiteadmin.datasource.biometric.local

import com.nullpointer.nullsiteadmin.actions.BiometricResult

interface BiometricDataSource {
    fun checkBiometricSupport(): Boolean
    suspend fun enableFingerBiometric(): BiometricResult
    suspend fun launchFingerBiometric(): BiometricResult
}