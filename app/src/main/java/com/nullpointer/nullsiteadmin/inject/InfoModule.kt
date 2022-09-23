package com.nullpointer.nullsiteadmin.inject

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nullpointer.nullsiteadmin.data.local.infoUser.InfoUserLocalDataSource
import com.nullpointer.nullsiteadmin.data.local.infoUser.InfoUserLocalDataSourceImpl
import com.nullpointer.nullsiteadmin.data.remote.infoUser.InfoUserRemoteDataSource
import com.nullpointer.nullsiteadmin.data.remote.infoUser.InfoUserRemoteDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InfoModule {

    @Provides
    @Singleton
    fun provideLocalInfoUser(
        dataStore: DataStore<Preferences>
    ): InfoUserLocalDataSource = InfoUserLocalDataSourceImpl(dataStore)

    @Provides
    @Singleton
    fun provideRemoteInfoUser(): InfoUserRemoteDataSource =
        InfoUserRemoteDataSourceImpl()

    @Provides
    @Singleton
    fun provideRemoteInfoRepo(
        infoUserDataSource: InfoUserRemoteDataSource,
        infoUserLocalDataSource: InfoUserLocalDataSource
    ): InfoUserRepoImpl = InfoUserRepoImpl(infoUserLocalDataSource, infoUserDataSource)
}