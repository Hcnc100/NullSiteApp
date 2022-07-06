package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.data.remote.project.ProjectDataSource
import com.nullpointer.nullsiteadmin.data.remote.project.ProjectDataSourceImpl
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
    fun provideProjectDataSource(): ProjectDataSource =
        ProjectDataSourceImpl()

    @Singleton
    @Provides
    fun provideProjectRepository(
        projectDataSource: ProjectDataSource
    ): ProjectRepoImpl = ProjectRepoImpl(projectDataSource)

}