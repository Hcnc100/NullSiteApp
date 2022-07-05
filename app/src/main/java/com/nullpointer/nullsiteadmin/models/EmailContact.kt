package com.nullpointer.nullsiteadmin.models

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
    val timestamp: Date? = null
) : Parcelable
