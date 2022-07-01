package com.nullpointer.nullsiteadmin.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type


class EmailDeserializer : JsonDeserializer<EmailContact?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): EmailContact {
        val jsonObject = json.asJsonObject
        val userObject = jsonObject["userInNotify"].asJsonObject
        return EmailContact(
            name = userObject["name"].asString,
            message = userObject["message"].asString,
            subject = userObject["subject"].asString,
            email = userObject["email"].asString,
        )
    }
}