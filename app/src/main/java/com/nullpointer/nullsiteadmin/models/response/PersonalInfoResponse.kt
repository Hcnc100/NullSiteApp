package com.nullpointer.nullsiteadmin.models.response

import java.util.Date

data class PersonalInfoResponse(
    val id: String,
    val name: String,
    val urlImg: String,
    val lastUpdate: Date,
    val profession: String,
    val description: String,
)