package com.nullpointer.nullsiteadmin.data.local.room

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromDate(date: Date?): Long = date?.time ?: 0

    @TypeConverter
    fun toDate(long: Long): Date = Date(long)
}