package com.nullpointer.nullsiteadmin.inject

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nullpointer.nullsiteadmin.data.infoPhone.local.CurrentInfoPhone
import com.nullpointer.nullsiteadmin.data.infoPhone.local.InfoPhoneDataStore
import com.nullpointer.nullsiteadmin.datasource.infoPhone.local.InfoPhoneLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.infoPhone.local.InfoPhoneLocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object InfoPhoneModule {


    @Provides
    @Singleton
    fun provideCurrentInfoPhone(
        @ApplicationContext context: Context
    ): CurrentInfoPhone = CurrentInfoPhone(context)


    @Provides
    @Singleton
    fun provideInfoPhoneDataStore(
        dataStore: DataStore<Preferences>
    ): InfoPhoneDataStore = InfoPhoneDataStore(dataStore)

    @Provides
    @Singleton
    fun provideInfoPhoneLocalDatSource(
        currentInfoPhone: CurrentInfoPhone,
        infoPhoneDataStore: InfoPhoneDataStore
    ): InfoPhoneLocalDataSource =
        InfoPhoneLocalDataSourceImpl(
            currentInfoPhone = currentInfoPhone,
            infoPhoneDataStore = infoPhoneDataStore
        )
}