package com.nullpointer.nullsiteadmin.data.editor

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import com.nullpointer.nullsiteadmin.domain.editor.EditorialRepository
import com.nullpointer.nullsiteadmin.exception.*
import com.nullpointer.nullsiteadmin.models.editor.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseEditorialRepository @Inject constructor() : EditorialRepository {
    private val db get() = Firebase.firestore
    private val drafts get() = db.collection("blog-drafts")
    private suspend fun requireAdmin(): String {
        val user = Firebase.auth.currentUser ?: throw NullAppException.AdminRequired
        if (user.getIdToken(false).await().claims["admin"] != true) throw NullAppException.AdminRequired
        return user.uid
    }
    override fun newId(): String = drafts.document().id
    override suspend fun load(): EditorialSnapshot {
        requireAdmin()
        val blog = drafts.orderBy(FieldPath.documentId()).limit(21).get(Source.SERVER).await()
        val legalDrafts = db.collection("legal-drafts").orderBy(FieldPath.documentId()).limit(21).get(Source.SERVER).await()
        val terms = db.collection("terms").orderBy(FieldPath.documentId()).limit(21).get(Source.SERVER).await()
        val settings = db.collection("siteSettings").document("public").get(Source.SERVER).await()
        requireAdmin()
        val privateById = legalDrafts.documents.take(20).associateBy { it.id }
        val publicById = terms.documents.take(20).associateBy { it.id }
        val records = (privateById.keys + publicById.keys).map { id ->
            val draft = privateById[id]; val published = publicById[id]
            LegalMetadata(id, (draft?.get("title") ?: published?.get("title")) as? String ?: id,
                published != null, draft != null, (draft?.get("revision") as? Number)?.toLong(),
                (draft?.get("publishedRevision") as? Number)?.toLong())
        }.sortedBy { it.title }
        return EditorialSnapshot(blog.documents.take(20).map { EditorialContract.readBlog(it.id, it.data.orEmpty()) }, records,
            settings.getBoolean("blogEnabled") == true, listOf(blog,legalDrafts,terms).any { it.size() > 20 })
    }
    override suspend fun saveMetadata(value: BlogMetadata) {
        val uid = requireAdmin(); require(EditorialContract.valid(value))
        val target = drafts.document(value.id)
        val slug = db.collection("blog-slugs").document("${value.locale}-${value.slug}")
        db.runTransaction { tx ->
            val current = tx.get(target); val raw = current.data
            EditorialContract.assertBaseline(raw, value.baseline)
            if (raw != null && !EditorialContract.readBlog(value.id, raw).compatible) throw UnsupportedBlogDraft()
            val reserved = tx.get(slug)
            val oldKey = raw?.let { "${it["locale"]}-${it["slug"]}" }
            val oldSlug = oldKey?.takeIf { it != slug.id }?.let { db.collection("blog-slugs").document(it) }
            val oldReservation = oldSlug?.let(tx::get)
            if (reserved.exists() && reserved.getString("draftId") != value.id) throw BlogSlugTaken()
            if (oldSlug != null && oldReservation?.getString("draftId") == value.id) tx.delete(oldSlug)
            tx.set(slug, mapOf("draftId" to value.id))
            val patch = EditorialContract.patch(value) + mapOf("updatedAt" to FieldValue.serverTimestamp())
            if (current.exists()) tx.update(target, patch)
            else tx.set(target, patch + mapOf("contentHtml" to EditorialContract.initialHtml(value.initialText), "contentFormat" to "html",
                "contentVersion" to 1, "status" to "draft", "authorUid" to uid, "createdAt" to FieldValue.serverTimestamp()))
        }.await()
    }
    override suspend fun history(id: String): List<LegalRevision> {
        requireAdmin(); require(Regex("^[a-zA-Z0-9_-]{1,100}$").matches(id))
        val docs = db.collection("legal-drafts").document(id).collection("history")
            .orderBy("publishedAt", Query.Direction.DESCENDING).limit(20).get(Source.SERVER).await()
        requireAdmin()
        return docs.documents.map { LegalRevision(it.id, it.getString("title").orEmpty(), (it.get("revision") as? Number)?.toLong() ?: 0) }
    }
}
