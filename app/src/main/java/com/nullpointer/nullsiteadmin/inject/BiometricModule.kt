package com.nullpointer.nullsiteadmin.inject

import android.content.Context
import com.nullpointer.nullsiteadmin.data.local.biometric.BiometricDataSource
import com.nullpointer.nullsiteadmin.data.local.biometric.BiometricDataSourceImpl
import com.nullpointer.nullsiteadmin.data.local.settings.SettingsDataSource
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepoImpl
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
        settingsDataSource: SettingsDataSource
    ): BiometricRepoImpl = BiometricRepoImpl(settingsDataSource, biometricDataSource)
}