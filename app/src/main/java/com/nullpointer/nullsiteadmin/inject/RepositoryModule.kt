package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.domain.InfoUserRepoImpl
import com.nullpointer.nullsiteadmin.domain.InfoUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideConfigRepository(
        infoUserRepoImpl: InfoUserRepoImpl
    ): InfoUserRepository
}