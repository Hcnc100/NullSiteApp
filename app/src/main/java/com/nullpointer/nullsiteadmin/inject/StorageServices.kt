package com.nullpointer.nullsiteadmin.inject

import android.content.Context
import com.nullpointer.nullsiteadmin.data.local.compress.ImageCompressDataSource
import com.nullpointer.nullsiteadmin.data.local.compress.ImageCompressDataSourceImpl
import com.nullpointer.nullsiteadmin.data.remote.storage.StorageDataSource
import com.nullpointer.nullsiteadmin.data.remote.storage.StorageDataSourceImpl
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
object StorageServices {

    @Provides
    @Singleton
    fun provideStorageDataSource(): StorageDataSource =
        StorageDataSourceImpl()

    @Provides
    @Singleton
    fun provideImageCompressRepository(
        @ApplicationContext context: Context
    ): ImageCompressDataSource = ImageCompressDataSourceImpl(context)

    @Provides
    @Singleton
    fun provideImgProfileRepo(
        storageDataSource: StorageDataSource,
        imageCompressDataSource: ImageCompressDataSource
    ): ImageRepository = ImageRepositoryImpl(
        storageDataSource = storageDataSource,
        imageCompressDataSource = imageCompressDataSource
    )
}