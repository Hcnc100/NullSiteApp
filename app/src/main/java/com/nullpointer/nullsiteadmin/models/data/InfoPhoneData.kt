package com.nullpointer.nullsiteadmin.models.data

import com.nullpointer.nullsiteadmin.interfaces.MappableFirebase

data class InfoPhoneData(
    val tokenGCM:String,
    val uuidPhone:String,
    val modelPhone:String,
    val operativeSystem:String,
    val versionOperativeSystem: String,
)
