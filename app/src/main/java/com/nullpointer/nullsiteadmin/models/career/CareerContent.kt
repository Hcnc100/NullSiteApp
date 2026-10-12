package com.nullpointer.nullsiteadmin.models.career

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import com.nullpointer.nullsiteadmin.exception.NullAppException
import com.google.firebase.Timestamp

@Serializable
data class SkillContent(
    val id: String = "", val name: String = "", val description: String = "",
    val category: String = "language", val iconKey: String = "android", val color: String = "#78C257",
    val sortOrder: String = "0", val published: Boolean = false,
    val translations: Map<String, Map<String, String>> = emptyMap(),
    val baseline: String? = null
)

@Serializable
data class ResumeContent(
    val storagePath: String = "", val fileName: String = "", val sizeBytes: Long = 0,
    val enabled: Boolean = false, val baseline: String = "null"
)

data class CareerSnapshot(
    val skills: List<SkillContent>, val skillsManaged: Boolean,
    val resumes: Map<String, ResumeContent>
)

object CareerContract {
    val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    val locales = listOf("es", "en")
    val icons = listOf("python", "java", "javascript", "android", "angular", "github", "git", "docker")
    private val keys = listOf("name", "description", "category", "iconKey", "color", "sortOrder", "published", "translations")
    const val MAX_PDF_BYTES = 10 * 1024 * 1024

    // Stable JSON fingerprints survive SavedState; maps are sorted, list order remains significant.
    fun canonical(value: Any?): String = element(value).toString()
    private fun element(value: Any?): JsonElement = when (value) {
        null -> JsonNull
        is Map<*, *> -> JsonObject(value.entries.sortedBy { it.key.toString() }.associate { it.key.toString() to element(it.value) })
        is Iterable<*> -> JsonArray(value.map(::element))
        is Boolean -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is Timestamp -> JsonObject(mapOf("seconds" to JsonPrimitive(value.seconds), "nanoseconds" to JsonPrimitive(value.nanoseconds)))
        else -> JsonPrimitive(value.toString())
    }
    fun skillFingerprint(data: Map<String, Any?>): String = canonical(keys.associateWith { data[it] })
    fun assertSkillBaseline(current: Map<String, Any?>?, baseline: String?) {
        if (if (baseline == null) current != null else current == null || skillFingerprint(current) != baseline)
            throw NullAppException.ContentConflict
    }
    fun assertResumeBaseline(current: Map<String, Any?>?, baseline: String) {
        if (canonical(current) != baseline) throw NullAppException.ContentConflict
    }
    fun valid(skill: SkillContent): Boolean = skill.name.isNotBlank() && skill.name.length <= 120 &&
        skill.description.length <= 10000 && skill.category in listOf("language", "tech") && skill.iconKey in icons &&
        Regex("^#[a-fA-F0-9]{6}$").matches(skill.color) && skill.sortOrder.toLongOrNull() in 0L..9999L &&
        locales.all { locale -> listOf("name", "description").all { field ->
            (skill.translations[locale]?.get(field)?.length ?: 0) <= if (field == "name") 120 else 10000
        } }

    fun skillPatch(skill: SkillContent): Map<String, Any> {
        require(valid(skill))
        return buildMap {
            put("name", skill.name.trim()); put("description", skill.description.trim())
            put("category", skill.category); put("iconKey", skill.iconKey); put("color", skill.color)
            put("sortOrder", skill.sortOrder.toLong()); put("published", skill.published)
            locales.forEach { locale -> listOf("name", "description").forEach { field ->
                put("translations.$locale.$field", skill.translations[locale]?.get(field)?.trim().orEmpty())
            } }
        }
    }
    fun skillCreate(skill: SkillContent): Map<String, Any> = skillPatch(skill).filterKeys { !it.startsWith("translations.") } +
        mapOf("id" to skill.id, "translations" to skill.translations.mapValues { (_, fields) -> fields.mapValues { it.value.trim() } })

    fun readSkill(id: String, data: Map<String, Any?>): SkillContent {
        val translations = (data["translations"] as? Map<*, *>)?.mapNotNull { (locale, raw) ->
            (raw as? Map<*, *>)?.let { locale.toString() to it.entries.filter { e -> e.value is String }.associate { e -> e.key.toString() to e.value as String } }
        }?.toMap().orEmpty()
        return SkillContent(id, data["name"] as? String ?: "", data["description"] as? String ?: "",
            data["category"] as? String ?: "", data["iconKey"] as? String ?: "", data["color"] as? String ?: "",
            (data["sortOrder"] as? Number)?.toLong()?.toString() ?: "", data["published"] == true, translations, skillFingerprint(data))
    }
    fun validPdf(name: String, mime: String?, bytes: ByteArray): Boolean = mime == "application/pdf" &&
        name.endsWith(".pdf", ignoreCase = true) && bytes.size in 6..MAX_PDF_BYTES &&
        bytes.take(5).toByteArray().contentEquals("%PDF-".toByteArray(Charsets.US_ASCII))
    fun validResumePath(locale: String, path: String): Boolean = locale in locales && Regex("^resumes/$locale/[a-zA-Z0-9_-]+\\.pdf$").matches(path)
    fun activeResume(locale: String, entry: ResumeContent): Boolean = entry.enabled && validResumePath(locale, entry.storagePath) && entry.sizeBytes in 1..MAX_PDF_BYTES.toLong()
    val defaultSkills: List<SkillContent> = listOf(
        listOf("python", "Python", "python", "#306998", "language"),
        listOf("java", "Java", "java", "#f89820", "language"),
        listOf("javascript", "JavaScript", "javascript", "#f0db4f", "language"),
        listOf("kotlin", "Kotlin", "android", "#78C257", "language"),
        listOf("dart", "Dart", "android", "#78C257", "language"),
        listOf("angular", "Angular", "angular", "#a6120d", "language"),
        listOf("github", "Github", "github", "#6e5494", "tech"),
        listOf("git", "Git", "git", "#f34f29", "tech"),
        listOf("docker", "Docker", "docker", "#0db7ed", "tech")
    ).mapIndexed { index, s -> SkillContent(s[0], s[1], category = s[4], iconKey = s[2], color = s[3], sortOrder = index.toString(), published = true) }
}
