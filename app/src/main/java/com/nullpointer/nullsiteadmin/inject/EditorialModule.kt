package com.nullpointer.nullsiteadmin.inject

import com.nullpointer.nullsiteadmin.data.editor.FirebaseEditorialRepository
import com.nullpointer.nullsiteadmin.domain.editor.EditorialRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class EditorialModule {
    @Binds abstract fun editorialRepository(repository: FirebaseEditorialRepository): EditorialRepository
}
