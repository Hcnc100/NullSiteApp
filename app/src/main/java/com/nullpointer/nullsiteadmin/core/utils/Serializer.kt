package com.nullpointer.nullsiteadmin.core.utils

import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


//convert a data class to a map
fun <T> T.toMap(
    listIgnoredFields: List<String> = emptyList(),
    listTimestampFields: List<String> = emptyList()
): Map<String, Any> {
    val gson = Gson()
    val json = gson.toJson(this)
    val previewMap = gson.fromJson<Map<String, Any>>(
        /* json = */ json,
        /* typeOfT = */ object : TypeToken<T>() {}.type
    ).toMutableMap()

    listTimestampFields.forEach {
        previewMap[it] = FieldValue.serverTimestamp()
    }

    listIgnoredFields.forEach {
        previewMap.remove(it)
    }
    return previewMap
}
