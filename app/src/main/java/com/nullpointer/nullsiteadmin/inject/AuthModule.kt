package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.data.local.settings.SettingsDataSource
import com.nullpointer.nullsiteadmin.data.remote.auth.AuthDataSource
import com.nullpointer.nullsiteadmin.data.remote.auth.AuthDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthDataSource(): AuthDataSource =
        AuthDataSourceImpl()

    @Provides
    @Singleton
    fun provideAuthRepository(
        authDataSource: AuthDataSource,
        settingsDataSource: SettingsDataSource
    ): AuthRepoImpl = AuthRepoImpl(authDataSource, settingsDataSource)
}