package com.nullpointer.nullsiteadmin.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val PROJECT_CONTENT_MIGRATION = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE projects ADD COLUMN contentJson TEXT NOT NULL DEFAULT '{}'")
    }
}
