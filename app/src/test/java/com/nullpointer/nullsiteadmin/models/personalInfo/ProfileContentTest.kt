package com.nullpointer.nullsiteadmin.models.personalInfo

import androidx.lifecycle.SavedStateHandle
import com.nullpointer.nullsiteadmin.core.utils.ContentEditGuard
import com.nullpointer.nullsiteadmin.exception.NullAppException
import com.nullpointer.nullsiteadmin.models.personalInfo.data.*
import com.nullpointer.nullsiteadmin.models.personalInfo.dto.PersonalInfoDTO
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.viewModel.ProfileContentEditorState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class ProfileContentTest {
    private val original = PersonalInfoData("Name", "https://example.com/p.png", 1, "Base profession", "Base biography")

    @Test fun old_cached_profiles_decode_with_absent_links_and_empty_translations() {
        val json = """{"name":"Name","urlImg":"https://example.com","updateAt":1,"profession":"Dev","description":"Bio"}"""
        val data = Json.decodeFromString<PersonalInfoData>(json)
        assertNull(data.socialLinks)
        assertTrue(data.translations.isEmpty())
    }

    @Test fun enriched_profile_cache_round_trip_preserves_all_languages_and_link_order() {
        val profile = original.copy(socialLinks = listOf(ProfileSocialLink("github", "https://github.com/user")),
            translations = mapOf("es" to mapOf("profession" to "Desarrollador"), "fr" to mapOf("description" to "Bio française")))
        assertEquals(profile, Json.decodeFromString<PersonalInfoData>(Json.encodeToString(profile)))
    }

    @Test fun absent_and_explicitly_empty_links_remain_different() {
        assertNull(ProfileContentContract.readLinks(null))
        assertEquals(emptyList<ProfileSocialLink>(), ProfileContentContract.readLinks(emptyList<Any>()))
        assertFalse(PersonalInfoDTO(null, null, null, null).toUpdateMap().containsKey("socialLinks"))
        assertEquals(emptyList<Any>(), PersonalInfoDTO(null, null, null, null, socialLinks = emptyList()).toUpdateMap()["socialLinks"])
    }

    @Test fun url_policy_rejects_credentials_http_scripts_and_overlong_links() {
        listOf("http://example.com", "javascript:alert(1)", "https://user:pass@example.com", "not a URL", "https://").forEach {
            assertFalse(it, ProfileContentContract.isHttpsUrl(it))
        }
        assertTrue(ProfileContentContract.isHttpsUrl("https://github.com/user"))
        assertFalse(ProfileContentContract.validLink(ProfileSocialLink("github", "https://example.com/" + "a".repeat(2048))))
    }

    @Test fun unsupported_platforms_duplicates_and_translation_overflow_are_invalid() {
        val link = ProfileSocialLink("github", "https://github.com/user")
        assertFalse(ProfileContentContract.isValid(ProfileContent(listOf(link, link))))
        assertFalse(ProfileContentContract.isValid(ProfileContent(listOf(link.copy(platform = "unknown")))))
        assertFalse(ProfileContentContract.isValid(ProfileContent(translations = mapOf("en" to mapOf("profession" to "a".repeat(181))))))
        assertFalse(ProfileContentContract.isValid(ProfileContent(translations = mapOf("en" to mapOf("description" to "a".repeat(5001))))))
        assertTrue(ProfileContentContract.isValid(ProfileContent(translations = mapOf("en" to mapOf("profession" to "a".repeat(180), "description" to "a".repeat(5000))))))
    }

    @Test fun localized_text_falls_back_only_for_missing_or_blank_translation() {
        assertEquals("Base", ProfileContentContract.localized("Base", emptyMap(), "es", "description"))
        assertEquals("Base", ProfileContentContract.localized("Base", mapOf("es" to mapOf("description" to "  ")), "es", "description"))
        assertEquals("English", ProfileContentContract.localized("Base", mapOf("en" to mapOf("description" to "English")), "en", "description"))
    }

    @Test fun update_patch_uses_dotted_paths_and_create_uses_nested_maps() {
        val dto = PersonalInfoDTO(null, null, null, null, translations = mapOf("en" to mapOf("profession" to "Developer")))
        assertEquals("Developer", dto.toUpdateMap()["translations.en.profession"])
        assertFalse(dto.toUpdateMap().containsKey("translations"))
        val created = dto.toCreateMap()
        assertFalse(created.keys.any { it.startsWith("translations.") })
        assertEquals("Developer", ((created["translations"] as Map<*, *>)["en"] as Map<*, *>)["profession"])
    }

    @Test fun known_translation_and_link_changes_are_conflicts_but_unknown_fields_are_not() {
        val baseline = ContentEditGuard.profileBaseline(original)!!
        val remote = mapOf<String, Any>("name" to original.name, "profession" to original.profession,
            "description" to original.description, "urlImg" to original.urlImg)
        ContentEditGuard.requireUnchanged(remote + ("translations" to mapOf("fr" to mapOf("description" to "Français"))), baseline)
        assertThrows(NullAppException.ContentConflict::class.java) {
            ContentEditGuard.requireUnchanged(remote + ("translations" to mapOf("en" to mapOf("description" to "Changed"))), baseline)
        }
        assertThrows(NullAppException.ContentConflict::class.java) {
            ContentEditGuard.requireUnchanged(remote + ("socialLinks" to emptyList<Any>()), baseline)
        }
    }

    @Test fun draft_restoration_preserves_both_languages_and_explicit_link_removal() {
        val saved = SavedStateHandle()
        val first = ProfileContentEditorState(saved)
        first.initialize(original)
        first.changeTranslation("profession", "Desarrollador")
        first.selectLocale("en")
        first.changeTranslation("profession", "Developer")
        first.clearSocialLinks()
        val restored = ProfileContentEditorState(SavedStateHandle(saved.keys().associateWith { saved.get<Any?>(it) }))
        restored.initialize(original.copy(profession = "Remote"))
        assertEquals("en", restored.locale)
        assertEquals("Developer", restored.translation("profession"))
        restored.selectLocale("es")
        assertEquals("Desarrollador", restored.translation("profession"))
        assertEquals(emptyList<ProfileSocialLink>(), restored.content.socialLinks)
        assertTrue(restored.hasChanged(original))
    }

    @Test fun editing_links_updates_in_place_and_can_remove_every_link() {
        val state = ProfileContentEditorState(SavedStateHandle())
        state.initialize(original)
        assertFalse(state.hasChanged(original))
        state.changeSocialUrl("github", "https://github.com/first")
        state.changeSocialUrl("github", "https://github.com/second")
        assertEquals(1, state.content.socialLinks!!.size)
        assertEquals("https://github.com/second", state.socialUrl("github"))
        state.changeSocialUrl("github", "")
        assertEquals(emptyList<ProfileSocialLink>(), state.content.socialLinks)
    }
}
