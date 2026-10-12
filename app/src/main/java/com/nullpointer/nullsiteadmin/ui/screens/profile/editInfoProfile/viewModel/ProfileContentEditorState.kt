package com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.viewModel

import androidx.lifecycle.SavedStateHandle
import com.nullpointer.nullsiteadmin.core.delagetes.SavableComposeState
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileContent
import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileContentContract
import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileSocialLink
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Serialized draft is Bundle-safe and does not confuse an absent link list with []. */
class ProfileContentEditorState(state: SavedStateHandle) {
    private var draftJson by SavableComposeState(state, "PROFILE_CONTENT_DRAFT", "")
    var locale by SavableComposeState(state, "PROFILE_CONTENT_LOCALE", "es")
        private set
    var showSocialLinks by SavableComposeState(state, "PROFILE_CONTENT_SHOW_LINKS", false)
        private set
    var showPreview by SavableComposeState(state, "PROFILE_CONTENT_SHOW_PREVIEW", false)
        private set

    val content: ProfileContent get() = draftJson.takeIf { it.isNotEmpty() }?.let {
        Json.decodeFromString<ProfileContent>(it)
    } ?: ProfileContent()
    val isValid get() = ProfileContentContract.isValid(content)

    fun initialize(profile: PersonalInfoData?) {
        if (draftJson.isEmpty()) draftJson = Json.encodeToString(ProfileContent(profile?.socialLinks, profile?.translations.orEmpty()))
    }

    fun selectLocale(value: String) {
        require(value in ProfileContentContract.locales)
        locale = value
    }
    fun toggleSocialLinks() { showSocialLinks = !showSocialLinks }
    fun togglePreview() { showPreview = !showPreview }
    fun translation(field: String) = content.translations[locale]?.get(field).orEmpty()
    fun socialUrl(platform: String) = content.socialLinks?.firstOrNull { it.platform == platform }?.url.orEmpty()

    fun changeTranslation(field: String, value: String) {
        require(field in ProfileContentContract.translatedFields)
        val current = content
        val translated = current.translations[locale].orEmpty() + (field to value)
        draftJson = Json.encodeToString(current.copy(translations = current.translations + (locale to translated)))
    }

    fun changeSocialUrl(platform: String, value: String) {
        require(platform in ProfileContentContract.platforms)
        val current = content
        val links = current.socialLinks.orEmpty().toMutableList()
        val index = links.indexOfFirst { it.platform == platform }
        if (value.isEmpty()) {
            if (index >= 0) links.removeAt(index)
        } else if (index >= 0) links[index] = ProfileSocialLink(platform, value)
        else links.add(ProfileSocialLink(platform, value))
        draftJson = Json.encodeToString(current.copy(socialLinks = links))
    }

    fun clearSocialLinks() { draftJson = Json.encodeToString(content.copy(socialLinks = emptyList())) }

    fun linksChanged(profile: PersonalInfoData?) = content.socialLinks != profile?.socialLinks
    fun translationsChanged(profile: PersonalInfoData?) = ProfileContentContract.locales.any { locale ->
        ProfileContentContract.translatedFields.any { field ->
            content.translations[locale]?.get(field).orEmpty() != profile?.translations?.get(locale)?.get(field).orEmpty()
        }
    }
    fun hasChanged(profile: PersonalInfoData?) = linksChanged(profile) || translationsChanged(profile)
}
