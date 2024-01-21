package com.nullpointer.nullsiteadmin.models.response

import java.util.Date

data class PersonalInfoResponse(
    val id: String = "",
    val name: String = "",
    val urlImg: String = "",
    val createAt: Date? = null,
    val updateAt: Date? = null,
    val profession: String = "",
    val description: String = "",
)