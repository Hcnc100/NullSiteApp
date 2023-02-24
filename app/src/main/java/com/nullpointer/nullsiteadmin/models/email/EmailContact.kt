package com.nullpointer.nullsiteadmin.models.email

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

@Serializable
@Entity(tableName = "emails")
data class EmailContact(
    @PrimaryKey
    val idEmail: String = "",
    val name: String = "",
    val message: String = "",
    val email: String = "",
    val subject: String = "",
    @Transient
    val timestamp: Date? = null,
    @field:JvmField
    val isOpen: Boolean = false
)
