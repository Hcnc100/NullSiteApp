package com.nullpointer.nullsiteadmin.core.utils

import com.google.firebase.firestore.FieldValue
import kotlin.reflect.full.memberProperties

/**
 * Interface MappableFirebase provides methods to convert an object into a map.
 * This can be useful when working with Firebase Firestore, where data is often represented as maps.
 */
interface MappableFirebase {

    /**
     * Converts the object into a mutable map.
     * The map's keys are the names of the object's properties, and the values are the properties' values.
     * Null values are filtered out.
     *
     * @return A mutable map representation of the object.
     */
    private fun toMutableMap(): MutableMap<String, Any> {
        return this::class.memberProperties
            .associate { it.name to it.getter.call(this) }
            .filter { it.value != null }
            .toMap() as MutableMap<String, Any>
    }

    /**
     * Converts the object into a map suitable for creating a document in Firestore.
     * In addition to the object's properties, the map includes 'createdAt' and 'updatedAt' timestamps.
     *
     * @return A map representation of the object, including 'createdAt' and 'updatedAt' timestamps.
     */
    fun toCreateMap(): Map<String, Any> {
        val prevMap = toMutableMap()
        prevMap[Constants.CREATED_AT] = FieldValue.serverTimestamp()
        prevMap[Constants.UPDATED_AT] = FieldValue.serverTimestamp()
        return prevMap.toMap()
    }

    /**
     * Converts the object into a map suitable for updating a document in Firestore.
     * In addition to the object's properties, the map includes an 'updatedAt' timestamp.
     *
     * @return A map representation of the object, including an 'updatedAt' timestamp.
     */
    fun toUpdateMap(): Map<String, Any> {
        val previewMap = toMutableMap()
        previewMap[Constants.UPDATED_AT] = FieldValue.serverTimestamp()
        return previewMap.toMap()
    }

}