package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.data.career.FirebaseCareerRepository
import com.nullpointer.nullsiteadmin.domain.career.CareerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CareerModule {
    @Binds abstract fun careerRepository(repository: FirebaseCareerRepository): CareerRepository
}
