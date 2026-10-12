package com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.composables.infoUser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileContentContract
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.socialPlatformName

@Composable
fun ProfileContentPreview(profile: PersonalInfoData, locale: String, modifier: Modifier = Modifier) {
    Card(modifier.fillMaxWidth().testTag("profile-content-preview")) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(profile.name, style = MaterialTheme.typography.titleLarge)
            Text(ProfileContentContract.localized(profile.profession, profile.translations, locale, "profession"),
                style = MaterialTheme.typography.titleMedium, modifier = Modifier.testTag("profile-preview-profession"))
            Text(ProfileContentContract.localized(profile.description, profile.translations, locale, "description"),
                style = MaterialTheme.typography.bodyLarge, modifier = Modifier.testTag("profile-preview-description"))
            if (ProfileContentContract.translatedFields.any { profile.translations[locale]?.get(it).isNullOrBlank() }) {
                Text(stringResource(R.string.profile_preview_fallback), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (profile.socialLinks == null) Text(stringResource(R.string.profile_legacy_links_help), style = MaterialTheme.typography.bodySmall)
            else if (profile.socialLinks.isEmpty()) Text(stringResource(R.string.profile_no_links), style = MaterialTheme.typography.bodySmall)
            else profile.socialLinks.forEach { link ->
                Column {
                    Text(socialPlatformName(link.platform), style = MaterialTheme.typography.labelLarge)
                    Text(link.url, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
