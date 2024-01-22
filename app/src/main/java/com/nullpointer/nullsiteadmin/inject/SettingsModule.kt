package com.nullpointer.nullsiteadmin.inject

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nullpointer.nullsiteadmin.data.settings.local.SettingsDataStore
import com.nullpointer.nullsiteadmin.datasource.settings.local.SettingsLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.settings.local.SettingsLocalDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.settings.SettingsRepoImpl
import com.nullpointer.nullsiteadmin.domain.settings.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    @Provides
    @Singleton
    fun provideSettingsDataStore(
        dataStore: DataStore<Preferences>
    ):SettingsDataStore = SettingsDataStore(
        dataStore = dataStore
    )

    @Provides
    @Singleton
    fun provideSettingsLocalDataSource(
        settingsDataStore: SettingsDataStore
    ): SettingsLocalDataSource = SettingsLocalDataSourceImpl(
        settingsDataStore = settingsDataStore
    )

    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsLocalDataSource: SettingsLocalDataSource
    ): SettingsRepository = SettingsRepoImpl(
        settingsLocalDataSource = settingsLocalDataSource
    )
}