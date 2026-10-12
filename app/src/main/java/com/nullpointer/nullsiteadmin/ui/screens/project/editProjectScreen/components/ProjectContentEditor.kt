package com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.project.data.*
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.ProfileLocaleSelector
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.viewModel.ProjectContentEditorState

@Composable
fun ProjectContentEditor(
    state: ProjectContentEditorState, enabled: Boolean, published: Boolean, onPublished: (Boolean) -> Unit,
    onUpload: () -> Unit, pending: Int, uploading: Boolean, progress: Float,
    onRetry: () -> Unit, onCancel: () -> Unit, onDiscardPending: () -> Unit,
) {
    val content = state.content
    Column(Modifier.fillMaxWidth().testTag("project-content-editor"), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HorizontalDivider()
        Text(stringResource(R.string.project_enriched), style = MaterialTheme.typography.titleLarge)
        Text(stringResource(R.string.project_privacy_notice), style = MaterialTheme.typography.bodySmall)
        Row { Switch(published, onPublished, enabled = enabled, modifier = Modifier.testTag("project-published"))
            Text(stringResource(R.string.project_publication), Modifier.padding(12.dp)) }
        ContentField(content.urlPlay.orEmpty(), R.string.project_demo, enabled, "project-demo") { state.change(content.copy(urlPlay = it)) }
        ContentField(state.technologiesText, R.string.project_technologies, enabled, "project-technologies", change = state::changeTechnologies)
        ContentField(content.coverAlt.orEmpty(), R.string.project_cover_alt, enabled, "project-cover-alt") { state.change(content.copy(coverAlt = it)) }
        ContentField(content.thumbnailUrl.orEmpty(), R.string.project_thumbnail, enabled, "project-thumbnail") { state.change(content.copy(thumbnailUrl = it)) }
        OutlinedTextField(state.orderText, state::changeOrder, enabled = enabled, label = { Text(stringResource(R.string.project_order)) },
            isError = state.orderText.isNotBlank() && state.orderText.toLongOrNull() == null, modifier = Modifier.fillMaxWidth())
        Text(stringResource(R.string.project_translations), style = MaterialTheme.typography.titleMedium)
        ProfileLocaleSelector(state.locale, enabled, state::selectLocale)
        listOf("name" to R.string.label_name_project, "description" to R.string.label_description_project, "coverAlt" to R.string.project_cover_alt).forEach { (field, label) ->
            ContentField(state.translation(field), label, enabled, "project-translation-$field", field == "description",
                prefix = if (state.locale == "es") "Español · " else "English · ") { state.changeTranslation(field, it) }
        }
        Text(stringResource(R.string.project_links), style = MaterialTheme.typography.titleMedium)
        content.links.orEmpty().forEachIndexed { index, link ->
            Card { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ContentField(link.label, R.string.project_link_label, enabled, "project-link-label-$index") { state.editLink(index, link.copy(label = it)) }
                ContentField(link.url, R.string.project_url, enabled, "project-link-url-$index") { state.editLink(index, link.copy(url = it)) }
                TextButton({ state.removeLink(index) }, enabled = enabled) { Text(stringResource(R.string.project_remove)) }
            } }
        }
        TextButton(state::addLink, enabled = enabled && content.links.orEmpty().size < 10, modifier = Modifier.testTag("project-add-link")) { Text(stringResource(R.string.project_add_link)) }
        Text(stringResource(R.string.project_gallery), style = MaterialTheme.typography.titleMedium)
        TextButton({ state.addImage() }, enabled = enabled && state.images.size + pending < 60, modifier = Modifier.testTag("project-add-capture")) { Text(stringResource(R.string.project_add_capture)) }
        TextButton(onUpload, enabled = enabled && state.images.size + pending < 60, modifier = Modifier.testTag("project-upload")) { Text(stringResource(R.string.project_upload_images)) }
        if (pending > 0) {
            Text(stringResource(R.string.project_pending, pending), Modifier.testTag("project-pending"))
            if (uploading) {
                LinearProgressIndicator(progress = { progress / 100f }, modifier = Modifier.fillMaxWidth())
                TextButton(onCancel) { Text(stringResource(R.string.project_cancel_upload)) }
            } else {
                TextButton(onRetry, enabled = enabled) { Text(stringResource(R.string.project_retry)) }
                TextButton(onDiscardPending, enabled = enabled) { Text(stringResource(R.string.project_clear_pending)) }
            }
        }
        state.images.forEachIndexed { index, image ->
            Card(Modifier.fillMaxWidth().testTag("project-capture-$index")) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${index + 1}", style = MaterialTheme.typography.titleMedium)
                    if (ProjectContentContract.https(image.url, false)) AsyncImage(
                        model = image.thumbnailUrl.takeIf { ProjectContentContract.https(it, false) } ?: image.url,
                        contentDescription = image.translations[state.locale]?.get("alt")?.takeIf(String::isNotBlank) ?: image.alt,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max = 160.dp))
                    ContentField(image.url, R.string.project_url, enabled, "project-capture-url-$index") { state.editImage(index, image.copy(url = it)) }
                    ContentField(image.alt, R.string.project_alt, enabled, "project-capture-alt-$index") { state.editImage(index, image.copy(alt = it)) }
                    ContentField(image.translations[state.locale]?.get("alt").orEmpty(), R.string.project_alt, enabled, "project-capture-translated-alt-$index",
                        prefix = if (state.locale == "es") "Español · " else "English · ") {
                        state.editImage(index, image.copy(translations = image.translations + (state.locale to (image.translations[state.locale].orEmpty() + ("alt" to it)))))
                    }
                    ContentField(image.thumbnailUrl, R.string.project_thumbnail, enabled, "project-capture-thumbnail-$index") { state.editImage(index, image.copy(thumbnailUrl = it)) }
                    TextButton({ state.moveImage(index, -1) }, enabled = enabled && index > 0, modifier = Modifier.testTag("project-up-$index")) { Text(stringResource(R.string.project_move_up)) }
                    TextButton({ state.moveImage(index, 1) }, enabled = enabled && index < state.images.lastIndex, modifier = Modifier.testTag("project-down-$index")) { Text(stringResource(R.string.project_move_down)) }
                    TextButton({ state.removeImage(index) }, enabled = enabled, modifier = Modifier.testTag("project-remove-$index")) { Text(stringResource(R.string.project_remove)) }
                }
            }
        }
        if (!state.isValid) Text(stringResource(R.string.project_invalid_content), color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("project-content-error"))
    }
}

@Composable private fun ContentField(value: String, label: Int, enabled: Boolean, tag: String, multiline: Boolean = false, prefix: String = "", change: (String) -> Unit) {
    val max = when (label) {
        R.string.project_alt, R.string.project_cover_alt -> 300
        R.string.label_name_project, R.string.project_link_label -> 120
        R.string.label_description_project -> 10000
        R.string.project_technologies -> null
        else -> 2048
    }
    val invalidUrl = label in listOf(R.string.project_url, R.string.project_demo, R.string.project_thumbnail) &&
        value.isNotBlank() && !ProjectContentContract.https(value)
    OutlinedTextField(value, change, enabled = enabled, label = { Text(prefix + stringResource(label), maxLines = 1, overflow = TextOverflow.Ellipsis) },
        isError = invalidUrl || (max != null && value.length > max),
        supportingText = if (max == null) null else ({ Text(if (invalidUrl) stringResource(R.string.profile_https_error) else "${value.length}/$max") }),
        singleLine = !multiline, minLines = if (multiline) 3 else 1, maxLines = if (multiline) 6 else 1,
        modifier = Modifier.fillMaxWidth().testTag(tag))
}
