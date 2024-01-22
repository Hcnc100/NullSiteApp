package com.nullpointer.nullsiteadmin.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nullpointer.nullsiteadmin.data.email.local.EmailDAO
import com.nullpointer.nullsiteadmin.data.project.local.ProjectDAO
import com.nullpointer.nullsiteadmin.models.email.entity.EmailEntity
import com.nullpointer.nullsiteadmin.models.project.entity.ProjectEntity


@Database(
    entities = [EmailEntity::class, ProjectEntity::class],
    version = 4,
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