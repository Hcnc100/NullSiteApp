package com.nullpointer.nullsiteadmin.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "projects")
@Parcelize
data class Project(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val urlImg: String = "",
    val urlRepo: String = "",
    val createdAt: Date? = null,
    val lastUpdate: Date? = null,
    val isVisible: Boolean = false
) : Parcelable