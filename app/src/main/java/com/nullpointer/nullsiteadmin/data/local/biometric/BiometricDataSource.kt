package com.nullpointer.nullsiteadmin.data.local.biometric

import com.nullpointer.nullsiteadmin.actions.BiometricResult

interface BiometricDataSource {
    fun checkBiometricSupport(): Boolean
    fun enableBiometric(callbackResult: (BiometricResult) -> Unit)
    fun launchBiometricInit(callbackResult: (BiometricResult) -> Unit)
}