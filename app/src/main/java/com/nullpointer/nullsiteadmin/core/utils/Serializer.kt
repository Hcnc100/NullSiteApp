package com.nullpointer.nullsiteadmin.core.utils

import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


//convert a data class to a map

interface MappableFirebase{
    fun toCreateMap(): Map<String, Any> {
        val gson = Gson()
        val json = gson.toJson(this)
        val previewMap = gson.fromJson<Map<String, Any>>(
            /* json = */ json,
            /* typeOfT = */ object : TypeToken<Any>() {}.type
        ).toMutableMap()

        previewMap[Constants.CREATE_AT] = FieldValue.serverTimestamp()
        previewMap[Constants.UPDATE_AT] = FieldValue.serverTimestamp()
        return previewMap
    }


    fun toUpdateMap(): Map<String, Any> {
        val gson = Gson()
        val json = gson.toJson(this)
        val previewMap = gson.fromJson<Map<String, Any>>(
            /* json = */ json,
            /* typeOfT = */ object : TypeToken<Any>() {}.type
        ).toMutableMap()

        previewMap[Constants.UPDATE_AT] = FieldValue.serverTimestamp()
        return previewMap
    }

}