package com.nullpointer.nullsiteadmin.models.settings.data

import kotlinx.serialization.Serializable


@Serializable
data class SettingsData(
    val timeOutLock:Long = 0,
    val isBiometricEnabled:Boolean = false,
)
