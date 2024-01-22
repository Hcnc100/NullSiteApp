package com.nullpointer.nullsiteadmin.inject

import android.content.Context
import com.nullpointer.nullsiteadmin.datasource.biometric.local.BiometricDataSource
import com.nullpointer.nullsiteadmin.datasource.biometric.local.BiometricDataSourceImpl
import com.nullpointer.nullsiteadmin.datasource.settings.local.SettingsLocalDataSource
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepoImpl
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BiometricModule {

    @Singleton
    @Provides
    fun providesBiometricDatSource(
        @ApplicationContext context: Context
    ): BiometricDataSource = BiometricDataSourceImpl(context)

    @Singleton
    @Provides
    fun providesBiometricRepository(
        biometricDataSource: BiometricDataSource,
        settingsLocalDataSource: SettingsLocalDataSource
    ): BiometricRepository = BiometricRepoImpl(
        settingsLocalDataSource = settingsLocalDataSource,
        biometricDataSource = biometricDataSource
    )
}