package com.nullpointer.nullsiteadmin.core.utils

import com.nullpointer.nullsiteadmin.models.personalInfo.dto.PersonalInfoDTO
import com.nullpointer.nullsiteadmin.models.project.dto.UpdateProjectDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class MobileWebContractTest {
    @Test fun project_patch_does_not_serialize_baseline_or_unknown_web_fields() {
        val patch = UpdateProjectDTO("Edited", null, null, null, null).toUpdateMap()
        assertEquals(setOf("name", Constants.UPDATED_AT), patch.keys)
        val remote = mapOf("translations" to mapOf("es" to mapOf("name" to "Español")),
            "gallery" to listOf("https://capture"), "urlPlay" to "https://play", "published" to true)
        val result = remote + patch
        remote.forEach { (key, value) -> assertEquals(value, result[key]) }
        assertEquals("Edited", result["name"])
    }

    @Test fun profile_patch_preserves_social_links_and_translations() {
        val patch = PersonalInfoDTO(null, null, null, "New biography").toUpdateMap()
        assertEquals(setOf("description", Constants.UPDATED_AT), patch.keys)
        assertFalse(patch.containsKey("baseline"))
        val remote = mapOf("socialLinks" to listOf(mapOf("url" to "https://github.com/user")),
            "translations" to mapOf("en" to mapOf("description" to "English biography")))
        val result = remote + patch
        remote.forEach { (key, value) -> assertEquals(value, result[key]) }
    }

    @Test fun limits_match_web_validators_by_resource_without_changing_login_limits() {
        assertEquals(120, FormLengthLimits.PROFILE_NAME)
        assertEquals(180, FormLengthLimits.PROFILE_PROFESSION)
        assertEquals(5000, FormLengthLimits.PROFILE_DESCRIPTION)
        assertEquals(120, FormLengthLimits.PROJECT_NAME)
        assertEquals(10000, FormLengthLimits.PROJECT_DESCRIPTION)
        assertEquals(2048, FormLengthLimits.URL)
        assertEquals(254, FormLengthLimits.EMAIL)
        assertEquals(4096, FormLengthLimits.MAX_CREDENTIAL_LENGTH)
    }
}
