package com.nullpointer.nullsiteadmin.models.biometric.data

import com.nullpointer.nullsiteadmin.actions.BiometricLockState


data class BiometricLockData(
    val timeOutLock: Long,
    val biometricLockState: BiometricLockState
)