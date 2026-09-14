package com.nullpointer.nullsiteadmin.domain.biometric

import com.nullpointer.nullsiteadmin.actions.BiometricLockState
import com.nullpointer.nullsiteadmin.actions.BiometricResultState
import com.nullpointer.nullsiteadmin.datasource.biometric.local.BiometricDataSource
import com.nullpointer.nullsiteadmin.datasource.settings.local.SettingsLocalDataSource
import com.nullpointer.nullsiteadmin.models.settings.data.SettingsData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class BiometricRepoImplTest {
    @Test
    fun init_without_biometric_support_marks_authentication_as_passed() = runTest {
        val repository = BiometricRepoImpl(FakeSettings(), FakeBiometric(false))

        repository.initVerifyBiometrics()

        assertEquals(true, repository.isAuthBiometricPassed.first())
    }

    @Test
    fun temporary_lock_sets_timeout_and_denies_access() = runTest {
        val settings = FakeSettings()
        val repository = BiometricRepoImpl(settings, FakeBiometric(true, BiometricResultState.TEMPORARILY_LOCKED))

        repository.unlockByBiometric()

        assertEquals(false, repository.isAuthBiometricPassed.first())
        assertEquals(true, settings.lastTimeout > System.currentTimeMillis())
    }

    private class FakeBiometric(private val supported: Boolean, private val result: BiometricResultState = BiometricResultState.PASSED) : BiometricDataSource {
        override fun checkBiometricSupport() = supported
        override suspend fun enableFingerBiometric() = result
        override suspend fun unlockByFingerBiometric() = result
    }

    private class FakeSettings : SettingsLocalDataSource {
        private val data = MutableStateFlow<SettingsData?>(SettingsData())
        var lastTimeout = 0L
        override fun getSettingsData(): Flow<SettingsData?> = data
        override suspend fun saveSettingsData(settingsData: SettingsData) { data.value = settingsData }
        override suspend fun clearData() { data.value = null }
        override suspend fun changeBiometricEnabled(isEnable: Boolean) { data.value = data.value?.copy(isBiometricEnabled = isEnable) }
        override suspend fun changeTimeOutLocked(timeOut: Long) { lastTimeout = timeOut; data.value = data.value?.copy(timeOutLock = timeOut) }
    }
}
