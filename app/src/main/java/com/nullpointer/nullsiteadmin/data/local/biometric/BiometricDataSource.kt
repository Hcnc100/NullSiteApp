package com.nullpointer.nullsiteadmin.data.local.biometric

interface BiometricDataSource {
    fun checkBiometricSupport(): Boolean
    fun enableBiometric(callbackResult: (Boolean) -> Unit)
    fun launchBiometricInit(callbackResult: (Boolean) -> Unit, callbackCancel: () -> Unit)
}