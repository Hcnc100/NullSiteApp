package com.nullpointer.nullsiteadmin.inject

import android.content.Context
import com.nullpointer.nullsiteadmin.data.image.remote.ImageApiServices
import com.nullpointer.nullsiteadmin.datasource.image.local.ImageLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.image.local.ImageLocalDataSourceImpl
import com.nullpointer.nullsiteadmin.datasource.image.remote.ImageRemoteDataSource
import com.nullpointer.nullsiteadmin.datasource.image.remote.ImageRemoteDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.storage.ImageRepository
import com.nullpointer.nullsiteadmin.domain.storage.ImageRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    @Provides
    @Singleton
    fun provideImageApiService(): ImageApiServices = ImageApiServices()

    @Provides
    @Singleton
    fun provideImageLocalDataSource(
        @ApplicationContext context: Context
    ): ImageLocalDataSource = ImageLocalDataSourceImpl(context)


    @Provides
    @Singleton
    fun provideImageRemoteDataSource(
        imageApiServices: ImageApiServices
    ): ImageRemoteDataSource = ImageRemoteDataSourceImpl(
        imageApiServices = imageApiServices
    )

    @Provides
    @Singleton
    fun provideImageRepository(
        imageLocalDataSource: ImageLocalDataSource,
        imageRemoteDataSource: ImageRemoteDataSource
    ): ImageRepository = ImageRepositoryImpl(
        imageLocalDataSource = imageLocalDataSource,
        imageRemoteDataSource = imageRemoteDataSource
    )
}