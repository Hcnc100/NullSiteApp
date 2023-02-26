package com.nullpointer.nullsiteadmin.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nullpointer.nullsiteadmin.models.Project
import com.nullpointer.nullsiteadmin.models.email.EmailContact


@Database(
    entities = [EmailContact::class, Project::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NullSiteDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "nullsite.db"
    }

    abstract fun getEmailDao(): EmailDAO
    abstract fun getProjectDao(): ProjectDAO
}