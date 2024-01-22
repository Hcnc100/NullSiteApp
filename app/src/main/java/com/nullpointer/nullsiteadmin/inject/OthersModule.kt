package com.nullpointer.nullsiteadmin.inject

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.nullpointer.nullsiteadmin.datasource.email.local.EmailLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.user.local.InfoUserLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.project.local.ProjectLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.settings.local.SettingsLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.settings.local.SettingsLocalDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.deleter.DeleterInfoRepository
import com.nullpointer.nullsiteadmin.domain.deleter.DeleterInfoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object OthersModule {

    private const val NAME_SETTINGS = "ADMIN_SETTINGS"

    @Singleton
    @Provides
    fun providePreferencesDataStore(
        @ApplicationContext appContext: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { appContext.preferencesDataStoreFile(NAME_SETTINGS) },
        )
    }



    @Singleton
    @Provides
    fun provideDeleterInfo(
        settingsLocalDataSource: SettingsLocalDataSource,
        emailLocalDataSource: EmailLocalDataSource,
        projectLocalDataSource: ProjectLocalDataSource,
        infoUserLocalDataSource: InfoUserLocalDataSource
    ): DeleterInfoRepository = DeleterInfoRepositoryImpl(
        settingsLocalDataSource = settingsLocalDataSource,
        emailLocalDataSource = emailLocalDataSource,
        projectLocalDataSource = projectLocalDataSource,
        infoUserLocalDataSource = infoUserLocalDataSource
    )


}