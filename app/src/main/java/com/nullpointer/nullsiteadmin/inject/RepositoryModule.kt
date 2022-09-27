package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.domain.auth.AuthRepoImpl
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepository
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepoImpl
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepository
import com.nullpointer.nullsiteadmin.domain.compress.CompressImgRepoImpl
import com.nullpointer.nullsiteadmin.domain.compress.CompressImgRepository
import com.nullpointer.nullsiteadmin.domain.deleter.DeleterInfoRepository
import com.nullpointer.nullsiteadmin.domain.deleter.DeleterInfoRepositoryImpl
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepoImpl
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepository
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepoImpl
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepository
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepoImpl
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepository
import com.nullpointer.nullsiteadmin.domain.storage.RepoImageProfileImpl
import com.nullpointer.nullsiteadmin.domain.storage.RepositoryImageProfile
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
    abstract fun provideInfoUserRepository(
        infoUserRepoImpl: InfoUserRepoImpl
    ): InfoUserRepository

    @Binds
    @Singleton
    abstract fun provideEmailRepository(
        emailsRepository: EmailsRepoImpl
    ): EmailsRepository

    @Binds
    @Singleton
    abstract fun provideProjectRepository(
        projectRepoImpl: ProjectRepoImpl
    ): ProjectRepository

    @Binds
    @Singleton
    abstract fun provideAuthRepository(
        authRepository: AuthRepoImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun provideImageProfileRepository(
        repoImageProfileImpl: RepoImageProfileImpl
    ): RepositoryImageProfile

    @Binds
    @Singleton
    abstract fun provideCompressRepository(
        compressRepoImpl: CompressImgRepoImpl
    ): CompressImgRepository

    @Binds
    @Singleton
    abstract fun provideDeleterRepository(
        deleterInfoRepoImpl: DeleterInfoRepositoryImpl
    ): DeleterInfoRepository

    @Binds
    @Singleton
    abstract fun provideBiometricRepository(
        biometricRepoImpl: BiometricRepoImpl
    ): BiometricRepository
}