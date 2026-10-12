package com.nullpointer.nullsiteadmin.domain.editor

import com.nullpointer.nullsiteadmin.models.editor.*

interface EditorialRepository {
    suspend fun load(): EditorialSnapshot
    fun newId(): String
    suspend fun saveMetadata(value: BlogMetadata)
    suspend fun history(id: String): List<LegalRevision>
}
