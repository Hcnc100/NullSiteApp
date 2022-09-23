package com.nullpointer.nullsiteadmin.core.utils

import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


//convert a data class to a map
fun <T> T.serializeToMap(
    vararg timestampFields: String
): Map<String, Any> {
    val gson = Gson()
    val json = gson.toJson(this)
    val previewMap =
        gson.fromJson<Map<String, Any>>(json, object : TypeToken<T>() {}.type).toMutableMap()
    timestampFields.forEach {
        previewMap[it] = FieldValue.serverTimestamp()
    }
    return previewMap
}
