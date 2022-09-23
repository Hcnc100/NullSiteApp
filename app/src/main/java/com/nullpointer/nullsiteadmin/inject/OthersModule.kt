package com.nullpointer.nullsiteadmin.inject

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.nullpointer.nullsiteadmin.data.local.email.EmailLocalDataSource
import com.nullpointer.nullsiteadmin.data.local.infoUser.InfoUserLocalDataSource
import com.nullpointer.nullsiteadmin.data.local.project.ProjectLocalDataSource
import com.nullpointer.nullsiteadmin.data.local.settings.SettingsDataSource
import com.nullpointer.nullsiteadmin.data.local.settings.SettingsDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.compress.CompressImgRepoImpl
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

    @Provides
    @Singleton
    fun provideSettingsDataSource(
        dataStore: DataStore<Preferences>
    ): SettingsDataSource = SettingsDataSourceImpl(dataStore)

    @Singleton
    @Provides
    fun provideCompress(
        @ApplicationContext context: Context
    ): CompressImgRepoImpl = CompressImgRepoImpl(context)

    @Singleton
    @Provides
    fun provideDeleterInfo(
        settingsDataSource: SettingsDataSource,
        infoUserLocalDataSource: InfoUserLocalDataSource,
        emailLocalDataSource: EmailLocalDataSource,
        projectLocalDataSource: ProjectLocalDataSource
    ): DeleterInfoRepositoryImpl = DeleterInfoRepositoryImpl(
        settingsDataSource, infoUserLocalDataSource, emailLocalDataSource, projectLocalDataSource
    )


}