package com.nullpointer.nullsiteadmin.models.editor

import com.nullpointer.nullsiteadmin.exception.NullAppException
import com.nullpointer.nullsiteadmin.models.career.CareerContract
import kotlinx.serialization.Serializable
import java.net.URI
import java.security.MessageDigest

@Serializable
data class BlogMetadata(
    val id: String = "", val title: String = "", val slug: String = "", val excerpt: String = "",
    val locale: String = "es", val tagsText: String = "", val revision: Long = 0,
    val publicationKey: String? = null, val publishedRevision: Long? = null,
    val baseline: String? = null, val initialText: String = "", val compatible: Boolean = true
)
data class LegalMetadata(val id: String, val title: String, val hasPublic: Boolean,
    val hasDraft: Boolean, val revision: Long?, val publishedRevision: Long?)
data class LegalRevision(val id: String, val title: String, val revision: Long)
data class EditorialSnapshot(val blogs: List<BlogMetadata>, val legal: List<LegalMetadata>,
    val blogEnabled: Boolean, val limited: Boolean)
enum class WebEditor(val path: String) { BLOG("/admin/blog"), LEGAL("/admin/legal") }

object EditorialContract {
    private const val MAX_SAFE_INTEGER = 9007199254740991L
    fun tags(value: BlogMetadata): List<String> = value.tagsText.split(',').map(String::trim).filter(String::isNotEmpty)
    fun valid(value: BlogMetadata): Boolean = Regex("^[a-zA-Z0-9_-]{1,128}$").matches(value.id) && value.compatible &&
        value.title.isNotBlank() && value.title.length <= 180 && value.excerpt.length <= 500 &&
        Regex("^[a-z0-9]+(-[a-z0-9]+)*$").matches(value.slug) && value.slug.length <= 100 && value.locale in listOf("es", "en") &&
        tags(value).let { it.size <= 20 && it.distinct().size == it.size && it.all { tag -> tag.length <= 40 } } &&
        value.revision in 0 until MAX_SAFE_INTEGER && (value.baseline != null || value.initialText.isNotBlank() && value.initialText.length <= 10000)

    fun initialHtml(text: String): String {
        require(text.isNotBlank() && text.length <= 10000)
        fun escape(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")
        return text.replace("\r\n", "\n").replace('\r', '\n').split('\n').joinToString("") { "<p>${escape(it)}</p>" }
    }
    fun fingerprint(raw: Map<String, Any?>): String {
        val known = listOf("title", "slug", "excerpt", "locale", "tags", "contentHtml", "revision", "publicationKey", "publishedRevision", "contentFormat", "contentVersion", "status")
        val bytes = CareerContract.canonical(known.associateWith { raw[it] }).toByteArray(Charsets.UTF_8)
        return MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    }
    fun assertBaseline(raw: Map<String, Any?>?, baseline: String?) {
        if (if (baseline == null) raw != null else raw == null || fingerprint(raw) != baseline) throw NullAppException.ContentConflict
    }
    fun readBlog(id: String, raw: Map<String, Any?>): BlogMetadata {
        val tags = raw["tags"] as? List<*>
        val revision = raw["revision"] as? Number
        return BlogMetadata(id, raw["title"] as? String ?: "", raw["slug"] as? String ?: "", raw["excerpt"] as? String ?: "",
            raw["locale"] as? String ?: "", tags?.joinToString(",") { it.toString() }.orEmpty(),
            (raw["revision"] as? Number)?.toLong() ?: -1, raw["publicationKey"] as? String, (raw["publishedRevision"] as? Number)?.toLong(),
            fingerprint(raw), compatible = raw["contentFormat"] == "html" && (raw["contentVersion"] as? Number)?.toDouble() == 1.0 &&
                revision != null && revision.toDouble() == revision.toLong().toDouble() && revision.toLong() in 0 until MAX_SAFE_INTEGER &&
                raw["status"] == "draft" && raw["contentHtml"] is String && (raw["contentHtml"] as String).length in 1..200000 &&
                tags != null && tags.all { it is String && !it.contains(',') })
    }
    fun patch(value: BlogMetadata): Map<String, Any> {
        require(valid(value))
        return mapOf("title" to value.title.trim(), "slug" to value.slug, "excerpt" to value.excerpt.trim(),
            "locale" to value.locale, "tags" to tags(value), "revision" to value.revision + 1)
    }
    // Only a fixed editor path on the build-configured HTTPS origin. No token, draft body or user URL is appended.
    fun editorUrl(base: String, editor: WebEditor): String? = runCatching {
        val uri = URI(base)
        require(uri.scheme == "https" && !uri.host.isNullOrBlank() && uri.rawUserInfo == null && uri.port in listOf(-1,443) &&
            uri.rawPath in listOf("", "/") && uri.rawQuery == null && uri.rawFragment == null)
        uri.resolve(editor.path).toASCIIString()
    }.getOrNull()
}
