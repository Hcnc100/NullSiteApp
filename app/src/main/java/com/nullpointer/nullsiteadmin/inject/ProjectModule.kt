package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.datasource.project.local.ProjectLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.project.local.ProjectLocalDataSourceImpl
import com.nullpointer.nullsiteadmin.database.NullSiteDatabase
import com.nullpointer.nullsiteadmin.data.project.local.ProjectDAO
import com.nullpointer.nullsiteadmin.data.project.remote.ProjectApiServices
import com.nullpointer.nullsiteadmin.datasource.project.remote.ProjectRemoteDataSource
import com.nullpointer.nullsiteadmin.datasource.project.remote.ProjectRemoteDataSourceImpl
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepoImpl
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepository
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
    fun provideProjectApiServices(): ProjectApiServices =
        ProjectApiServices()

    @Singleton
    @Provides
    fun provideProjectLocalDataSource(
        projectDAO: ProjectDAO
    ): ProjectLocalDataSource = ProjectLocalDataSourceImpl(projectDAO)


    @Singleton
    @Provides
    fun provideProjectDataSource(
        projectApiServices: ProjectApiServices
    ): ProjectRemoteDataSource =
        ProjectRemoteDataSourceImpl(
            projectApiServices = projectApiServices
        )

    @Singleton
    @Provides
    fun provideProjectRepository(
        projectRemoteDataSource: ProjectRemoteDataSource,
        projectLocalDataSource: ProjectLocalDataSource
    ): ProjectRepository = ProjectRepoImpl(
        projectLocalDataSource = projectLocalDataSource,
        projectRemoteDataSource = projectRemoteDataSource
    )

}