package com.nullpointer.nullsiteadmin.datasource.biometric.local

import com.nullpointer.nullsiteadmin.actions.BiometricResultState

interface BiometricDataSource {
    fun checkBiometricSupport(): Boolean
    suspend fun enableFingerBiometric(): BiometricResultState
    suspend fun unlockByFingerBiometric(): BiometricResultState
}