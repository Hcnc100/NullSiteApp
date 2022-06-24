package com.nullpointer.nullsiteadmin.models

import java.util.*

data class EmailContact(
    val id: String = "",
    val name: String = "",
    val message: String = "",
    val email: String = "",
    val subject: String = "",
    val timestamp: Date? = null
)
