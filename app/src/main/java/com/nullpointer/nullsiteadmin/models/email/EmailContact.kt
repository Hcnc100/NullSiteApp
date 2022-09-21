package com.nullpointer.nullsiteadmin.models.email

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "emails")
data class EmailContact(
    @get:Exclude
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val message: String = "",
    val email: String = "",
    val subject: String = "",
    val timestamp: Date? = null,
    @field:JvmField
    val isOpen: Boolean = false
) : Parcelable
