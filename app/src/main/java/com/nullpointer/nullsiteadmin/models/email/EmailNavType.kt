package com.nullpointer.nullsiteadmin.models.email

import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@NavTypeSerializer
class EmailNavType : DestinationsNavTypeSerializer<EmailContact> {
    override fun toRouteString(value: EmailContact): String = Json.encodeToString(value)
    override fun fromRouteString(routeStr: String): EmailContact = Json.decodeFromString(routeStr)
}