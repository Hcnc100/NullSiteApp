package com.nullpointer.nullsiteadmin.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "projects")
@Parcelize
data class Project(
    @get:Exclude
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val urlImg: String = "",
    val urlRepo: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val lastUpdate: Date? = null,
    val isVisible: Boolean = false
) : Parcelable