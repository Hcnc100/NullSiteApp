package com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.utils.FormLengthLimits
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileContentContract
import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileSocialLink
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.viewModel.ProfileContentEditorState
import com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.composables.infoUser.ProfileContentPreview

@Composable
fun ProfileLocaleSelector(locale: String, enabled: Boolean = true, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("es" to "Español", "en" to "English").forEach { (code, label) ->
            FilterChip(selected = locale == code, onClick = { onSelect(code) }, enabled = enabled,
                label = { Text(label) }, modifier = Modifier.testTag("profile-locale-$code"))
        }
    }
}

@Composable
fun ProfileContentEditor(
    state: ProfileContentEditorState,
    name: String,
    profession: String,
    description: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HorizontalDivider()
        Text(stringResource(R.string.profile_translations), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.profile_translation_help), style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        ProfileLocaleSelector(state.locale, enabled, state::selectLocale)
        listOf("profession" to R.string.label_profession_admin, "description" to R.string.label_description_admin).forEach { (field, label) ->
            val text = state.translation(field)
            val max = if (field == "profession") FormLengthLimits.PROFILE_PROFESSION else FormLengthLimits.PROFILE_DESCRIPTION
            OutlinedTextField(
                value = text, onValueChange = { state.changeTranslation(field, it) }, enabled = enabled,
                label = { Text("${if (state.locale == "es") "Español" else "English"} · ${stringResource(label)}") },
                singleLine = field == "profession", minLines = if (field == "profession") 1 else 3,
                maxLines = if (field == "profession") 1 else 6,
                isError = text.length > max,
                supportingText = { Text(if (text.length > max) stringResource(R.string.profile_length_error, max) else "${text.length}/$max") },
                modifier = Modifier.fillMaxWidth().testTag("profile-translation-$field"),
            )
        }
        HorizontalDivider()
        TextButton(onClick = state::toggleSocialLinks, enabled = enabled, modifier = Modifier.testTag("profile-toggle-links")) {
            Text(stringResource(if (state.showSocialLinks) R.string.profile_hide_social_links else R.string.profile_social_links))
        }
        if (state.showSocialLinks) {
            Text(stringResource(if (state.content.socialLinks == null) R.string.profile_legacy_links_help else R.string.profile_links_help),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            ProfileContentContract.platforms.forEach { platform ->
                val url = state.socialUrl(platform)
                val invalid = url.isNotEmpty() && !ProfileContentContract.validLink(ProfileSocialLink(platform, url))
                OutlinedTextField(value = url, onValueChange = { state.changeSocialUrl(platform, it) },
                    enabled = enabled, label = { Text(socialPlatformName(platform)) }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri), isError = invalid,
                    supportingText = { Text(if (invalid) stringResource(R.string.profile_https_error) else "${url.length}/${FormLengthLimits.URL}") },
                    trailingIcon = if (url.isNotEmpty()) ({
                        TextButton(onClick = { state.changeSocialUrl(platform, "") }, enabled = enabled) {
                            Text(stringResource(R.string.profile_remove_link))
                        }
                    }) else null,
                    modifier = Modifier.fillMaxWidth().testTag("profile-social-$platform"),
                )
            }
            TextButton(onClick = state::clearSocialLinks, enabled = enabled, modifier = Modifier.testTag("profile-clear-links")) {
                Text(stringResource(R.string.profile_clear_links))
            }
        }
        HorizontalDivider()
        TextButton(onClick = state::togglePreview, enabled = enabled, modifier = Modifier.testTag("profile-toggle-preview")) {
            Text(stringResource(if (state.showPreview) R.string.profile_hide_preview else R.string.profile_preview))
        }
        if (state.showPreview) ProfileContentPreview(
            profile = PersonalInfoData(name, "", 0, profession, description, state.content.socialLinks, state.content.translations),
            locale = state.locale,
        )
    }
}

fun socialPlatformName(platform: String) = when (platform) {
    "github" -> "GitHub"
    "twitter" -> "Twitter / X"
    "whatsapp" -> "WhatsApp"
    "facebook" -> "Facebook"
    "instagram" -> "Instagram"
    else -> platform
}
