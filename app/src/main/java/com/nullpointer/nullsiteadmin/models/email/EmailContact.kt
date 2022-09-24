package com.nullpointer.nullsiteadmin.models.email

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "emails")
data class EmailContact(
    @PrimaryKey
    val idEmail: String = "",
    val name: String = "",
    val message: String = "",
    val email: String = "",
    val subject: String = "",
    val timestamp: Date? = null,
    @field:JvmField
    val isOpen: Boolean = false
) : Parcelable
