package com.nullpointer.nullsiteadmin.inject

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nullpointer.nullsiteadmin.data.auth.local.AuthDataStore
import com.nullpointer.nullsiteadmin.data.auth.remote.AuthApiServices
import com.nullpointer.nullsiteadmin.datasource.auth.local.AuthLocalDatSourceImpl
import com.nullpointer.nullsiteadmin.datasource.auth.local.AuthLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.auth.remote.AuthRemoteDataSource
import com.nullpointer.nullsiteadmin.datasource.auth.remote.AuthRemoteDataSourceImpl
import com.nullpointer.nullsiteadmin.datasource.infoPhone.local.InfoPhoneLocalDataSource
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepoImpl
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepository
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
    fun provideAuthApiServices(): AuthApiServices = AuthApiServices()


    @Provides
    @Singleton
    fun provideAuthDataStore(
        dataStore: DataStore<Preferences>
    ): AuthDataStore = AuthDataStore(dataStore = dataStore)

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(
        authApiServices: AuthApiServices
    ): AuthRemoteDataSource = AuthRemoteDataSourceImpl(
        authApiServices = authApiServices
    )


    @Provides
    @Singleton
    fun provideAuthLocalDataSource(
        authDataStore: AuthDataStore
    ):AuthLocalDataSource= AuthLocalDatSourceImpl(
        authDataStore = authDataStore
    )

    @Provides
    @Singleton
    fun provideAuthRepository(
        authLocalDataSource: AuthLocalDataSource,
        authRemoteDataSource: AuthRemoteDataSource,
        infoPhoneLocalDataSource: InfoPhoneLocalDataSource
    ): AuthRepository = AuthRepoImpl(
        authLocalDataSource = authLocalDataSource,
        authRemoteDataSource = authRemoteDataSource,
        infoPhoneLocalDataSource = infoPhoneLocalDataSource

    )
}