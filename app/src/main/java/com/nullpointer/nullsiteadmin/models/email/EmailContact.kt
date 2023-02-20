package com.nullpointer.nullsiteadmin.models.email

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nullpointer.nullsiteadmin.models.DateSerializer
import kotlinx.serialization.Serializable
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
    @Serializable(with = DateSerializer::class)
    val timestamp: Date? = null,
    @field:JvmField
    val isOpen: Boolean = false
)
