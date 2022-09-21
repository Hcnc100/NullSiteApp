package com.nullpointer.nullsiteadmin.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nullpointer.nullsiteadmin.models.email.EmailContact


@Database(
    entities = [EmailContact::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NullSiteDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "nullsite.db"
    }

    abstract fun getEmailDao(): EmailDao
}