package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.datasource.email.local.EmailLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.email.local.EmailLocalDataSourceImpl
import com.nullpointer.nullsiteadmin.data.email.local.EmailDAO
import com.nullpointer.nullsiteadmin.datasource.email.remote.EmailRemoteDataSource
import com.nullpointer.nullsiteadmin.datasource.email.remote.EmailRemoteDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepoImpl
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepository
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
        emailDao: EmailDAO
    ): EmailLocalDataSource = EmailLocalDataSourceImpl(emailDao)

    @Provides
    @Singleton
    fun provideEmailRemoteDataSource(): EmailRemoteDataSource =
        EmailRemoteDataSourceImpl()

    @Singleton
    @Provides
    fun provideEmailRepository(
        emailRemoteDataSource: EmailRemoteDataSource,
        emailLocalDataSource: EmailLocalDataSource
    ): EmailsRepository = EmailsRepoImpl(
        emailRemoteDataSource = emailRemoteDataSource,
        emailLocalDataSource = emailLocalDataSource
    )
}