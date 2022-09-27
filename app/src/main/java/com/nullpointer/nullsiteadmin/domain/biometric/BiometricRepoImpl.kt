package com.nullpointer.nullsiteadmin.domain.biometric

import com.nullpointer.nullsiteadmin.data.local.biometric.BiometricDataSource
import com.nullpointer.nullsiteadmin.data.local.settings.SettingsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking

class BiometricRepoImpl(
    private val settingsDataSource: SettingsDataSource,
    private val biometricDataSource: BiometricDataSource
) : BiometricRepository {
    override val isBiometricEnabled: Flow<Boolean> = settingsDataSource.isBiometricEnabled()

    override fun checkBiometricSupport(): Boolean =
        biometricDataSource.checkBiometricSupport()

    override suspend fun changeIsBiometricEnabled(newValue: Boolean) {
        if (newValue) {
            biometricDataSource.enableBiometric { success ->
                runBlocking {
                    if (success) settingsDataSource.changeBiometricEnabled(true)
                }
            }
        } else {
            settingsDataSource.changeBiometricEnabled(false)
        }
    }

    override fun launchBiometric(
        callbackResult: (Boolean) -> Unit,
        callbackCancel: () -> Unit
    ) = biometricDataSource.launchBiometricInit(
        callbackResult = callbackResult,
        callbackCancel = callbackCancel
    )
}