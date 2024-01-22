package com.nullpointer.nullsiteadmin.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nullpointer.nullsiteadmin.data.email.local.EmailDAO
import com.nullpointer.nullsiteadmin.models.Project
import com.nullpointer.nullsiteadmin.models.email.EmailData
import com.nullpointer.nullsiteadmin.models.email.EmailEntity


@Database(
    entities = [EmailEntity::class, Project::class],
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