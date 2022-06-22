package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.data.remote.InfoUserDataSource
import com.nullpointer.nullsiteadmin.data.remote.InfoUserDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.InfoUserRepoImpl
import com.nullpointer.nullsiteadmin.domain.InfoUserRepository
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
    fun provideInfoUser(): InfoUserDataSource =
        InfoUserDataSourceImpl()

    @Provides
    @Singleton
    fun provideInfoRepo(
        infoUserDataSource: InfoUserDataSource
    ): InfoUserRepoImpl = InfoUserRepoImpl(infoUserDataSource)
}