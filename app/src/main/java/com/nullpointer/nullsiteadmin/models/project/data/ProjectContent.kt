package com.nullpointer.nullsiteadmin.models.project.data

import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileContentContract
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

@Serializable data class ProjectLink(val label: String = "", val url: String = "")
@Serializable data class ProjectImage(
    val url: String = "", val alt: String = "", val order: Long = 0,
    val thumbnailUrl: String = "",
    val translations: Map<String, Map<String, String>> = emptyMap(),
    val extra: Map<String, JsonElement> = emptyMap(),
    val presentFields: Set<String>? = null,
)
@Serializable data class ProjectContent(
    val urlPlay: String? = null,
    val links: List<ProjectLink>? = null,
    val technologyIds: List<String>? = null,
    val gallery: List<String>? = null,
    val galleryItems: List<ProjectImage>? = null,
    val coverAlt: String? = null,
    val thumbnailUrl: String? = null,
    val translations: Map<String, Map<String, String>> = emptyMap(),
    val sortOrder: Long? = null,
)

object ProjectContentContract {
    val json = Json { ignoreUnknownKeys = true }
    val fields = listOf("name", "description", "coverAlt")
    fun encode(content: ProjectContent) = json.encodeToString(content)
    fun decode(value: String) = json.decodeFromString<ProjectContent>(value)
    fun https(value: String?, optional: Boolean = true) =
        if (value.isNullOrBlank()) optional else value.length <= 2048 && ProfileContentContract.isHttpsUrl(value)
    fun valid(content: ProjectContent): Boolean = with(content) {
        https(urlPlay) && https(thumbnailUrl) && coverAlt.orEmpty().length <= 300 &&
            links.orEmpty().size <= 10 && links.orEmpty().all { it.label.isNotBlank() && it.label.length <= 120 && https(it.url, false) } &&
            technologyIds.orEmpty().size <= 30 && technologyIds.orEmpty().all { it.isNotBlank() && it.length <= 120 } &&
            1 + effectiveGallery(content).size + links.orEmpty().size + (if (urlPlay.isNullOrBlank()) 0 else 1) <= 60 &&
            effectiveGallery(content).map { it.url }.distinct().size == effectiveGallery(content).size &&
            gallery.orEmpty().size <= 60 && gallery.orEmpty().all { https(it, false) } &&
            galleryItems.orEmpty().size <= 60 && galleryItems.orEmpty().all { image ->
                https(image.url, false) && https(image.thumbnailUrl) && image.alt.length <= 300 &&
                    listOf("es", "en").all { image.translations[it]?.get("alt").orEmpty().length <= 300 }
            } && listOf("es", "en").all { locale -> fields.all { field ->
                translations[locale]?.get(field).orEmpty().length <= when (field) { "name" -> 120; "description" -> 10000; else -> 300 }
            } }
    }

    fun effectiveGallery(content: ProjectContent): List<ProjectImage> {
        val metadata = content.galleryItems.orEmpty().sortedBy { it.order }
        val urls = content.gallery ?: metadata.map { it.url }
        return urls.mapIndexed { index, url -> metadata.firstOrNull { it.url == url }?.copy(order = index.toLong())
            ?: ProjectImage(url = url, order = index.toLong()) }
    }

    private fun imageMap(image: ProjectImage): Map<String, Any?> = image.extra.mapValues { native(it.value) } + mapOf(
        "url" to image.url, "alt" to image.alt, "order" to image.order,
        "thumbnailUrl" to image.thumbnailUrl, "translations" to image.translations,
    ).filterKeys { image.presentFields == null || it in image.presentFields }
    private fun native(element: JsonElement): Any? = when (element) {
        JsonNull -> null
        is JsonObject -> element.mapValues { native(it.value) }
        is JsonArray -> element.map(::native)
        is JsonPrimitive -> if (element.isString) element.content else element.booleanOrNull ?: element.longOrNull ?: element.doubleOrNull
    }
    fun baseline(content: ProjectContent): Map<String, Any?> = mapOf(
        "urlPlay" to content.urlPlay, "links" to content.links?.map { mapOf("label" to it.label, "url" to it.url) },
        "technologyIds" to content.technologyIds, "gallery" to content.gallery,
        "galleryItems" to content.galleryItems?.map(::imageMap), "coverAlt" to content.coverAlt,
        "thumbnailUrl" to content.thumbnailUrl, "sortOrder" to content.sortOrder,
    ) + listOf("es", "en").flatMap { locale -> fields.map { field ->
        "translations.$locale.$field" to content.translations[locale]?.get(field).orEmpty()
    } }.toMap()

    fun patch(content: ProjectContent, original: ProjectContent): Map<String, Any> {
        require(valid(content))
        val before = baseline(original)
        return baseline(content).filter { (key, value) -> value != null && value != before[key] }
            .mapValues { it.value!! }
    }
    fun create(content: ProjectContent): Map<String, Any> {
        require(valid(content))
        return baseline(content).filter { (key, value) -> value != null && !key.startsWith("translations.") }
            .mapValues { it.value!! } + ("translations" to content.translations)
    }

    /** Parse enrichment independently of Firestore's legacy bean conversion. */
    fun read(raw: Map<String, Any?>): ProjectContent {
        fun strings(key: String) = (raw[key] as? List<*>)?.mapNotNull { it as? String }
        fun translations(value: Any?) = ProfileContentContract.readTranslations(value)
        val images = (raw["galleryItems"] as? List<*>)?.mapNotNull { value ->
            val map = value as? Map<*, *> ?: return@mapNotNull null
            val url = map["url"] as? String ?: return@mapNotNull null
            ProjectImage(url, map["alt"] as? String ?: "", (map["order"] as? Number)?.toLong() ?: 0,
                map["thumbnailUrl"] as? String ?: "", translations(map["translations"]),
                map.entries.filter { it.key is String && it.key !in listOf("url", "alt", "order", "thumbnailUrl", "translations") }
                    .associate { it.key as String to element(it.value) }, map.keys.filterIsInstance<String>().toSet())
        }
        return ProjectContent(raw["urlPlay"] as? String,
            (raw["links"] as? List<*>)?.mapNotNull { value ->
                val map = value as? Map<*, *> ?: return@mapNotNull null
                ProjectLink(map["label"] as? String ?: "", map["url"] as? String ?: "")
            }, strings("technologyIds"), strings("gallery"), images,
            raw["coverAlt"] as? String, raw["thumbnailUrl"] as? String,
            translations(raw["translations"]), (raw["sortOrder"] as? Number)?.toLong())
    }
    private fun element(value: Any?): JsonElement = when (value) {
        null -> JsonNull
        is Map<*, *> -> JsonObject(value.entries.filter { it.key is String }.associate { it.key as String to element(it.value) })
        is List<*> -> JsonArray(value.map(::element))
        is Boolean -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        else -> JsonPrimitive(value.toString())
    }
}
