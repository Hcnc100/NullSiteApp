package com.nullpointer.nullsiteadmin.models.email

import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer
import java.util.*

@NavTypeSerializer
class EmailNavType : DestinationsNavTypeSerializer<EmailContact> {

    override fun toRouteString(value: EmailContact): String {
        return "${value.idEmail};${value.name};${value.email};${value.subject};${value.message};${value.timestamp?.time}"
    }

    override fun fromRouteString(routeStr: String): EmailContact {
        val things = routeStr.split(";")
        return EmailContact(
            idEmail = things[0],
            name = things[1],
            email = things[2],
            subject = things[3],
            message = things[4],
            timestamp = Date(things[5].toLong())
        )
    }
}