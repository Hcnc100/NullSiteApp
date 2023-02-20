package com.nullpointer.nullsiteadmin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.serialization.Serializable
import java.util.*

@Entity(tableName = "projects")
@Serializable
data class Project(
    @get:Exclude
    @PrimaryKey
    val idProject: String = "",
    val name: String = "",
    val description: String = "",
    val urlImg: String = "",
    val urlRepo: String = "",
    @Serializable(with = DateSerializer::class)
    @ServerTimestamp
    val createdAt: Date? = null,
    @Serializable(with = DateSerializer::class)
    @ServerTimestamp
    val lastUpdate: Date? = null,
    val isVisible: Boolean = false
)