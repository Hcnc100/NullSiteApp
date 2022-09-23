package com.nullpointer.nullsiteadmin.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class PersonalInfo(
    @get:Exclude
    val id: String = "",
    val name: String = "",
    val profession: String = "",
    val description: String = "",
    val urlImg: String = "",
    @ServerTimestamp
    val lastUpdate: Date? = null,
) : Parcelable
