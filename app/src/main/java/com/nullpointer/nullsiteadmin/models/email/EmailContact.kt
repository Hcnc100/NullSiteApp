package com.nullpointer.nullsiteadmin.models.email

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class EmailContact(
    val id: String = "",
    val name: String = "",
    val message: String = "",
    val email: String = "",
    val subject: String = "",
    val timestamp: Date? = null,
    @field:JvmField
    val isOpen: Boolean = false
) : Parcelable