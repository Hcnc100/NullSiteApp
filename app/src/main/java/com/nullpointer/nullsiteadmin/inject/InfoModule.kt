package com.nullpointer.nullsiteadmin.inject

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nullpointer.nullsiteadmin.datasource.user.local.InfoUserLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.user.local.InfoUserLocalDataSourceImpl
import com.nullpointer.nullsiteadmin.data.local.services.ServicesManager
import com.nullpointer.nullsiteadmin.data.user.local.UserDataStore
import com.nullpointer.nullsiteadmin.datasource.auth.local.AuthLocalDataSource
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
    )= UserDataStore(dataStore = dataStore)


    @Provides
    @Singleton
    fun provideLocalInfoUser(
        userDataStore: UserDataStore
    ): InfoUserLocalDataSource = InfoUserLocalDataSourceImpl(userDataStore)

    @Provides
    @Singleton
    fun provideRemoteInfoUser(): InfoUserRemoteDataSource =
        InfoUserRemoteDataSourceImpl()

    @Provides
    @Singleton
    fun provideServicesManager(
        @ApplicationContext context: Context
    ): ServicesManager = ServicesManager(context)

    @Provides
    @Singleton
    fun provideRemoteInfoRepo(
        authLocalDataSource: AuthLocalDataSource,
        infoUserDataSource: InfoUserRemoteDataSource,
        infoUserLocalDataSource: InfoUserLocalDataSource,
    ): InfoUserRepository = InfoUserRepoImpl(
        authLocalDataSource = authLocalDataSource,
        infoUserRemoteDataSource = infoUserDataSource,
        infoUserLocalDataSource = infoUserLocalDataSource,
    )
}