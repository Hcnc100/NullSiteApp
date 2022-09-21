package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.data.local.email.EmailLocalDataSource
import com.nullpointer.nullsiteadmin.data.local.email.EmailLocalDataSourceImpl
import com.nullpointer.nullsiteadmin.data.local.room.EmailDao
import com.nullpointer.nullsiteadmin.data.remote.email.EmailRemoteDataSource
import com.nullpointer.nullsiteadmin.data.remote.email.EmailRemoteDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmailModule {

    @Singleton
    @Provides
    fun provideEmailLocalDataSource(
        emailDao: EmailDao
    ): EmailLocalDataSource = EmailLocalDataSourceImpl(emailDao)

    @Provides
    @Singleton
    fun provideEmailRemoteDataSource(): EmailRemoteDataSource =
        EmailRemoteDataSourceImpl()

    @Singleton
    @Provides
    fun provideEmailRepository(
        emailDataSource: EmailRemoteDataSource
    ): EmailsRepoImpl = EmailsRepoImpl(emailDataSource)
}