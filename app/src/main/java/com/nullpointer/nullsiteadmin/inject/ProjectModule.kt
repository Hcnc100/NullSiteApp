package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.data.local.project.ProjectLocalDataSource
import com.nullpointer.nullsiteadmin.data.local.project.ProjectLocalDataSourceImpl
import com.nullpointer.nullsiteadmin.data.local.room.NullSiteDatabase
import com.nullpointer.nullsiteadmin.data.local.room.ProjectDAO
import com.nullpointer.nullsiteadmin.data.remote.project.ProjectRemoteDataSource
import com.nullpointer.nullsiteadmin.data.remote.project.ProjectRemoteDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProjectModule {

    @Singleton
    @Provides
    fun provideProjectDAO(
        nullSiteDatabase: NullSiteDatabase
    ): ProjectDAO = nullSiteDatabase.getProjectDao()

    @Singleton
    @Provides
    fun provideProjectLocalDataSource(
        projectDAO: ProjectDAO
    ): ProjectLocalDataSource = ProjectLocalDataSourceImpl(projectDAO)


    @Singleton
    @Provides
    fun provideProjectDataSource(): ProjectRemoteDataSource =
        ProjectRemoteDataSourceImpl()

    @Singleton
    @Provides
    fun provideProjectRepository(
        projectRemoteDataSource: ProjectRemoteDataSource,
        projectLocalDataSource: ProjectLocalDataSource
    ): ProjectRepoImpl = ProjectRepoImpl(projectLocalDataSource, projectRemoteDataSource)

}