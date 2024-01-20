package com.nullpointer.nullsiteadmin.models.dto

import com.google.firebase.firestore.FieldValue

data class UpdateProjectDTO(
    val name: String?,
    val urlImg: String?,
    val urlRepo: String?,
    val isVisible: Boolean?,
    val description: String?
){
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        name?.let { map["name"] = it }
        urlImg?.let { map["urlImg"] = it }
        urlRepo?.let { map["urlRepo"] = it }
        isVisible?.let { map["isVisible"] = it }
        description?.let { map["description"] = it }
        map["lastUpdate"] = FieldValue.serverTimestamp()
        return map
    }
}

