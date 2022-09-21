package com.nullpointer.nullsiteadmin.inject

import android.content.Context
import com.nullpointer.nullsiteadmin.data.local.settings.SettingsDataSource
import com.nullpointer.nullsiteadmin.data.local.settings.SettingsDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.compress.CompressImgRepoImpl
import com.nullpointer.nullsiteadmin.domain.compress.CompressImgRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OthersModule {
    @Provides
    @Singleton
    fun provideSettingsDataSource(
        @ApplicationContext context: Context
    ): SettingsDataSource = SettingsDataSourceImpl(context)

    @Singleton
    @Provides
    fun provideCompress(
        @ApplicationContext context: Context
    ): CompressImgRepository = CompressImgRepoImpl(context)


}