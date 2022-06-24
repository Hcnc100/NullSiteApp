package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.domain.email.EmailsRepoImpl
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepository
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepoImpl
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepository
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

    @Binds
    @Singleton
    abstract fun provideEmailRepository(
        emailsRepository: EmailsRepoImpl
    ): EmailsRepository
}