package com.nullpointer.nullsiteadmin.models.email

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.util.*


class EmailDeserializer : JsonDeserializer<EmailData?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): EmailData {
        val jsonObject = json.asJsonObject
        return EmailData(
            name = jsonObject["name"].asString,
            message = jsonObject["message"].asString,
            subject = jsonObject["subject"].asString,
            email = jsonObject["email"].asString,
            idEmail = jsonObject["id"].asString,
        )
    }
}