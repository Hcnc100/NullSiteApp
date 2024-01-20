package com.nullpointer.nullsiteadmin.models.data

import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.serialization.Transient
import java.util.Date

data class ProjectData(
    val id: String,
    val name: String,
    val urlImg: String,
    val urlRepo: String,
    val createdAt: Date,
    val lastUpdate: Date,
    val description: String,
    val isVisible: Boolean = false
)
