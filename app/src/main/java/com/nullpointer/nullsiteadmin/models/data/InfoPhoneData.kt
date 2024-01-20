package com.nullpointer.nullsiteadmin.models.data

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import kotlinx.serialization.Serializable


@Serializable
data class InfoPhoneData(
    val tokenGCM:String,
    val uuidPhone:String,
    val modelPhone:String,
    val versionNameApp:String,
    val operativeSystem:String,
    val versionNumberApp:String,
)
