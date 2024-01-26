package com.nullpointer.nullsiteadmin.core.utils

import com.google.firebase.firestore.FieldValue
import kotlin.reflect.full.memberProperties


//convert a data class to a map

interface MappableFirebase{
    fun toCreateMap(): Map<String, Any> {
        val prevMap = this::class.memberProperties
            .associate { it.name to it.getter.call(this) }
            .filter { it.value != null }
            .toMutableMap()
        prevMap[Constants.CREATED_AT] = FieldValue.serverTimestamp()
        prevMap[Constants.UPDATED_AT] = FieldValue.serverTimestamp()
        return prevMap.toMap() as Map<String, Any>
    }


    fun toUpdateMap(): Map<String, Any> {
        val previewMap = this::class.memberProperties
            .associate { it.name to it.getter.call(this) }
            .filter { it.value != null }
            .toMutableMap()
        previewMap[Constants.UPDATED_AT] = FieldValue.serverTimestamp()
        return previewMap.toMap() as Map<String, Any>
    }

}