package com.nullpointer.nullsiteadmin.models.personalInfo.data

import com.nullpointer.nullsiteadmin.core.utils.FormLengthLimits
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class ProfileSocialLink(val platform: String, val url: String)

@Serializable
data class ProfileContent(
    // Absence keeps the web's historical links; an empty list explicitly removes them.
    val socialLinks: List<ProfileSocialLink>? = null,
    val translations: Map<String, Map<String, String>> = emptyMap(),
)

object ProfileContentContract {
    val platforms = listOf("github", "facebook", "instagram", "twitter", "whatsapp")
    val locales = listOf("es", "en")
    val translatedFields = listOf("profession", "description")

    fun isHttpsUrl(value: String): Boolean = try {
        val uri = URI(value.trim())
        uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank() && uri.rawUserInfo == null
    } catch (_: Exception) { false }

    fun validLink(link: ProfileSocialLink) = link.platform in platforms &&
        link.url.length <= FormLengthLimits.URL && isHttpsUrl(link.url)

    fun isValid(content: ProfileContent): Boolean {
        val links = content.socialLinks.orEmpty()
        return links.size <= platforms.size && links.distinctBy { it.platform }.size == links.size &&
            links.all(::validLink) && locales.all { locale ->
                translatedFields.all { field ->
                    content.translations[locale]?.get(field).orEmpty().length <=
                        if (field == "profession") FormLengthLimits.PROFILE_PROFESSION else FormLengthLimits.PROFILE_DESCRIPTION
                }
            }
    }

    fun readLinks(raw: Any?): List<ProfileSocialLink>? {
        if (raw !is List<*>) return null
        return raw.mapNotNull { item ->
            val data = item as? Map<*, *> ?: return@mapNotNull null
            val platform = data["platform"] as? String ?: return@mapNotNull null
            val url = data["url"] as? String ?: return@mapNotNull null
            ProfileSocialLink(platform, url).takeIf(::validLink)
        }.distinctBy { it.platform }
    }

    fun readTranslations(raw: Any?): Map<String, Map<String, String>> =
        (raw as? Map<*, *>)?.entries?.mapNotNull { (locale, value) ->
            if (locale !is String || value !is Map<*, *>) return@mapNotNull null
            locale to value.entries.mapNotNull { (key, text) ->
                if (key is String && text is String) key to text else null
            }.toMap()
        }?.toMap().orEmpty()

    fun localized(base: String, translations: Map<String, Map<String, String>>, locale: String, field: String) =
        translations[locale]?.get(field)?.takeIf { it.isNotBlank() } ?: base

    fun linkMaps(links: List<ProfileSocialLink>) = links.map {
        mapOf("platform" to it.platform, "url" to it.url.trim())
    }

    /** Dotted updates preserve translations belonging to other fields/locales. */
    fun patch(links: List<ProfileSocialLink>?, translations: Map<String, Map<String, String>>?): Map<String, Any> {
        require(isValid(ProfileContent(links, translations.orEmpty()))) { "Invalid profile content" }
        return buildMap {
            links?.let { put("socialLinks", linkMaps(it)) }
            translations?.let { data ->
                for (locale in locales) for (field in translatedFields) {
                    put("translations.$locale.$field", data[locale]?.get(field).orEmpty().trim())
                }
            }
        }
    }
}
