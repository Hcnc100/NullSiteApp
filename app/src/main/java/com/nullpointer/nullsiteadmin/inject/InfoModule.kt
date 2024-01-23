package com.nullpointer.nullsiteadmin.inject

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nullpointer.nullsiteadmin.data.services.ServicesManager
import com.nullpointer.nullsiteadmin.data.user.local.UserDataStore
import com.nullpointer.nullsiteadmin.data.user.remote.UserApiServices
import com.nullpointer.nullsiteadmin.datasource.auth.local.AuthLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.image.remote.ImageRemoteDataSource
import com.nullpointer.nullsiteadmin.datasource.user.local.InfoUserLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.user.local.InfoUserLocalDataSourceImpl
import com.nullpointer.nullsiteadmin.datasource.user.remote.InfoUserRemoteDataSource
import com.nullpointer.nullsiteadmin.datasource.user.remote.InfoUserRemoteDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepoImpl
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InfoModule {

    @Provides
    @Singleton
    fun provideUserDataStore(
        dataStore: DataStore<Preferences>
    ):UserDataStore = UserDataStore(dataStore = dataStore)


    @Provides
    @Singleton
    fun provideUserApiServices(): UserApiServices = UserApiServices()


    @Provides
    @Singleton
    fun provideInfoUserLocalDataSource(
        userDataStore: UserDataStore
    ): InfoUserLocalDataSource = InfoUserLocalDataSourceImpl(userDataStore)

    @Provides
    @Singleton
    fun provideInfoUserRemoteDataSource(
        userApiServices: UserApiServices
    ): InfoUserRemoteDataSource =
        InfoUserRemoteDataSourceImpl(userApiServices = userApiServices)

    @Provides
    @Singleton
    fun provideServicesManager(
        @ApplicationContext context: Context
    ): ServicesManager = ServicesManager(context)

    @Provides
    @Singleton
    fun provideRemoteInfoRepo(
        authLocalDataSource: AuthLocalDataSource,
        imageRemoteDataSource: ImageRemoteDataSource,
        infoUserLocalDataSource: InfoUserLocalDataSource,
        infoUserRemoteDataSource: InfoUserRemoteDataSource,
    ): InfoUserRepository = InfoUserRepoImpl(
        authLocalDataSource = authLocalDataSource,
        imageRemoteDataSource = imageRemoteDataSource,
        infoUserLocalDataSource = infoUserLocalDataSource,
        infoUserRemoteDataSource = infoUserRemoteDataSource
    )
}