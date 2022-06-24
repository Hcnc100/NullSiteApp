package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.data.remote.email.EmailDataSource
import com.nullpointer.nullsiteadmin.data.remote.email.EmailDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmailModule {

    @Provides
    @Singleton
    fun provideEmailDataSource(): EmailDataSource =
        EmailDataSourceImpl()

    @Singleton
    @Provides
    fun provideEmailRepository(
        emailDataSource: EmailDataSource
    ): EmailsRepoImpl = EmailsRepoImpl(emailDataSource)
}