package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.data.remote.storage.StorageDataSource
import com.nullpointer.nullsiteadmin.data.remote.storage.StorageDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.storage.RepoImageProfileImpl
import com.nullpointer.nullsiteadmin.domain.storage.RepositoryImageProfile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideImgProfileRepo(
        storageDataSource: StorageDataSource
    ):RepoImageProfileImpl= RepoImageProfileImpl(storageDataSource)
}