package com.nullpointer.nullsiteadmin.inject

import android.content.Context
import androidx.room.Room
import com.nullpointer.nullsiteadmin.data.email.local.EmailDAO
import com.nullpointer.nullsiteadmin.database.NullSiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): NullSiteDatabase = Room.databaseBuilder(
        context,
        NullSiteDatabase::class.java,
        NullSiteDatabase.DATABASE_NAME
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideEmailDAO(
        nullSiteDatabase: NullSiteDatabase
    ): EmailDAO = nullSiteDatabase.getEmailDao()


}