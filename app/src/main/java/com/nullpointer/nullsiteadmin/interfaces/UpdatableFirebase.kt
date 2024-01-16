package com.nullpointer.nullsiteadmin.interfaces

import com.google.firebase.firestore.FieldValue
import com.nullpointer.nullsiteadmin.core.utils.Constants

interface MappableFirebase {

    fun toUpdateMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        this::class.members.forEach { prop ->
            val value = prop.call(this)
            if (value != null) {
                map[prop.name] = value
            }
        }
        map[Constants.nameFieldUpdate] = FieldValue.serverTimestamp()
        return map
    }

    fun toCreateMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        this::class.members.forEach { prop ->
            val value = prop.call(this)
            if (value != null) {
                map[prop.name] = value
            }
        }
        map[Constants.nameFieldCreate] = FieldValue.serverTimestamp()
        map[Constants.nameFieldUpdate] = FieldValue.serverTimestamp()
        return map
    }


}