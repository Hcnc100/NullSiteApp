package com.nullpointer.nullsiteadmin.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PersonalInfo(
    val name: String = "",
    val profession: String = "",
    val description: String = "",
    val urlImg: String = "",
    @get:Exclude
    val idPersonal: String = "",
    @ServerTimestamp
    @Serializable(with = DateSerializer::class)
    val lastUpdate: Date? = null,
)
