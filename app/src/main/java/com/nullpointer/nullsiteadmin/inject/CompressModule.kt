package com.nullpointer.nullsiteadmin.inject

import android.content.Context
import com.nullpointer.nullsiteadmin.domain.compress.CompressImgRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CompressModule {

    @Singleton
    @Provides
    fun provideCompress(
        @ApplicationContext context: Context
    ): CompressImgRepoImpl = CompressImgRepoImpl(context)
}