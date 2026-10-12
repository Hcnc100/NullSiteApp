package com.nullpointer.nullsiteadmin.core.utils

import com.nullpointer.nullsiteadmin.exception.NullAppException
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileContentContract
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.models.project.data.ProjectContentContract

/** Compare the original form values, not a fresh read taken just before saving.
 * Unknown web fields are intentionally excluded: partial updates preserve them.
 */
object ContentEditGuard {
    fun profileBaseline(profile: PersonalInfoData?): Map<String, Any?>? = profile?.let {
        mapOf("name" to it.name, "profession" to it.profession,
            "description" to it.description, "urlImg" to it.urlImg,
            "socialLinks" to it.socialLinks?.let(ProfileContentContract::linkMaps)) +
            ProfileContentContract.locales.flatMap { locale ->
                ProfileContentContract.translatedFields.map { field ->
                    "translations.$locale.$field" to it.translations[locale]?.get(field).orEmpty()
                }
            }.toMap()
    }

    fun projectBaseline(project: ProjectData): Map<String, Any?> = mapOf(
        "name" to project.name, "description" to project.description,
        "urlImg" to project.urlImg, "urlRepo" to project.urlRepo,
        "published" to project.isVisible,
    ) + ProjectContentContract.baseline(project.content)

    /** Null means the document did not exist when editing began. */
    fun requireUnchanged(current: Map<String, Any?>?, baseline: Map<String, Any?>?) {
        if (baseline == null) {
            if (current != null) throw NullAppException.ContentConflict
            return
        }
        if (current == null || baseline.any { (key, expected) ->
                // Older documents may omit optional strings or the visibility flag.
                val raw = key.split('.').fold(current as Any?) { value, part -> (value as? Map<*, *>)?.get(part) }
                val actual = raw ?: when (expected) {
                    is Boolean -> false
                    is String -> ""
                    else -> null
                }
                actual != expected
            }) {
            throw NullAppException.ContentConflict
        }
    }
}
