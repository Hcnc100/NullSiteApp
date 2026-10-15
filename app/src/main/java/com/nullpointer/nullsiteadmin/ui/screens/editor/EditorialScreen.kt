package com.nullpointer.nullsiteadmin.ui.screens.editor

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.BuildConfig
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.editor.*
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.share.AppPullToRefreshBox
import com.nullpointer.nullsiteadmin.ui.share.rememberAppRefreshState
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph
@Destination
@Composable
fun EditorialScreen(viewModel: EditorialViewModel = hiltViewModel()) {
    val context = LocalContext.current
    EditorialContent(viewModel) { editor ->
        val url = EditorialContract.editorUrl(BuildConfig.URL_MAIN_PAGE, editor)
        if (url == null) viewModel.browserFailed()
        else runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }.onFailure { viewModel.browserFailed() }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorialContent(vm: EditorialViewModel, openEditor: (WebEditor) -> Unit) {
    var confirmation by remember { mutableStateOf<String?>(null) }
    val refreshState = rememberAppRefreshState(vm.refreshing, vm::refresh)
    BackHandler(vm.busy || vm.draft != null) { if (!vm.busy) confirmation = "discard" }
    Surface(Modifier.fillMaxSize()) {
        AppPullToRefreshBox(refreshState, Modifier.fillMaxSize().testTag("editor-refresh-container"),
            enabled = vm.draft == null && (!vm.busy || vm.refreshing)) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.editor_title), style = MaterialTheme.typography.headlineSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("blog" to R.string.editor_blog, "legal" to R.string.editor_legal).forEach { (key,label) ->
                    FilterChip(selected = vm.section == key, enabled = !vm.busy && vm.draft == null,
                        onClick = { vm.section(key) }, label = { Text(stringResource(label)) }, modifier = Modifier.testTag("editor-tab-$key"))
                }
            }
            Text(stringResource(R.string.editor_hybrid_help))
            OutlinedButton(onClick = { confirmation = "web" }, enabled = !vm.busy, modifier = Modifier.testTag("editor-open-web")) { Text(stringResource(R.string.editor_open_web)) }
            if (vm.busy && !vm.refreshing) LinearProgressIndicator(Modifier.fillMaxWidth())
            vm.error?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("editor-error")) }
            if (vm.saved) Text(stringResource(R.string.editor_saved), modifier = Modifier.testTag("editor-saved"))
            if (vm.snapshot?.limited == true) Text(stringResource(R.string.editor_limited))
            if (vm.snapshot == null) {
                if (!vm.busy && vm.error != null) TextButton(onClick = vm::refresh,
                    modifier = Modifier.testTag("editor-retry")) { Text(stringResource(R.string.career_reload)) }
            } else if (vm.section == "blog") {
                Text(stringResource(if (vm.snapshot!!.blogEnabled) R.string.editor_blog_enabled else R.string.editor_blog_disabled))
                val draft = vm.draft
                if (draft == null) {
                    Button(onClick = vm::create, enabled = !vm.busy && !vm.blocked, modifier = Modifier.testTag("editor-new")) { Text(stringResource(R.string.editor_new)) }
                    if (vm.snapshot!!.blogs.isEmpty()) Text(stringResource(R.string.editor_empty))
                    vm.snapshot!!.blogs.forEach { item ->
                        OutlinedCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(item.title, style = MaterialTheme.typography.titleMedium)
                                Text("${item.locale} · ${item.slug}")
                                Text(stringResource(R.string.editor_draft_revision, item.revision))
                                if (item.publicationKey != null) Text(stringResource(R.string.editor_public_reference, item.publicationKey, item.publishedRevision ?: 0))
                                else Text(stringResource(R.string.editor_private))
                                TextButton(onClick = { vm.edit(item) }, enabled = !vm.busy && !vm.blocked,
                                    modifier = Modifier.testTag("editor-edit-${item.id}")) { Text(stringResource(R.string.editor_edit_metadata)) }
                            }
                        }
                    }
                } else {
                    BlogEditor(vm, draft)
                    Button(onClick = vm::save, enabled = vm.canSave, modifier = Modifier.testTag("editor-save")) { Text(stringResource(R.string.editor_save)) }
                    TextButton(onClick = { confirmation = "discard" }, enabled = !vm.busy, modifier = Modifier.testTag("editor-discard")) { Text(stringResource(R.string.career_close)) }
                }
            } else {
                Text(stringResource(R.string.editor_legal_help))
                if (vm.snapshot!!.legal.isEmpty()) Text(stringResource(R.string.editor_empty))
                vm.snapshot!!.legal.forEach { item ->
                    OutlinedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text(item.id)
                            Text(stringResource(if (item.hasPublic) R.string.editor_public_included else R.string.editor_public_missing))
                            if (item.hasDraft) Text(stringResource(R.string.editor_legal_revision, item.revision ?: 0, item.publishedRevision ?: 0))
                            else Text(stringResource(R.string.editor_draft_missing))
                            TextButton(onClick = { vm.history(item.id) }, enabled = !vm.busy && item.hasDraft && Regex("^[a-zA-Z0-9_-]{1,100}$").matches(item.id),
                                modifier = Modifier.testTag("editor-history-${item.id}")) { Text(stringResource(R.string.editor_history)) }
                        }
                    }
                }
                vm.history?.let { entries ->
                    Text(stringResource(R.string.editor_history_for, vm.historyId.orEmpty()), style = MaterialTheme.typography.titleMedium)
                    if (entries.isEmpty()) Text(stringResource(R.string.editor_history_empty))
                    entries.forEach { Text(stringResource(R.string.editor_history_row, it.revision, it.title), modifier = Modifier.testTag("editor-history-row-${it.id}")) }
                }
            }
        }
        }
    }
    confirmation?.let { action ->
        val title = stringResource(if (action == "web") R.string.editor_open_web else R.string.project_discard_title)
        val explanation = stringResource(if (action == "web") R.string.editor_session_help else R.string.career_confirm_help)
        val yes = stringResource(R.string.career_confirm); val no = stringResource(R.string.profile_keep_editing)
        AlertDialog(onDismissRequest = { confirmation = null }, title = { Text(title) },
            text = { Column(Modifier.verticalScroll(rememberScrollState())) { Text(explanation) } },
            confirmButton = { TextButton(onClick = {
                confirmation = null
                if (action == "web") openEditor(if (vm.section == "blog") WebEditor.BLOG else WebEditor.LEGAL) else vm.discard()
            }, modifier = Modifier.testTag("editor-confirm")) { Text(yes) } },
            dismissButton = { TextButton(onClick = { confirmation = null }) { Text(no) } })
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BlogEditor(vm: EditorialViewModel, draft: BlogMetadata) {
    val enabled = !vm.busy
    EditorField(draft.title, R.string.editor_name, "editor-name", 180, enabled) { vm.update(draft.copy(title = it)) }
    EditorField(draft.slug, R.string.editor_slug, "editor-slug", 100, enabled) { vm.update(draft.copy(slug = it)) }
    EditorField(draft.excerpt, R.string.editor_excerpt, "editor-excerpt", 500, enabled, true) { vm.update(draft.copy(excerpt = it)) }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("es","en").forEach { locale -> FilterChip(selected = draft.locale == locale, enabled = enabled,
            onClick = { vm.update(draft.copy(locale = locale)) }, label = { Text(if (locale == "es") "Español" else "English") }, modifier = Modifier.testTag("editor-locale-$locale")) }
    }
    EditorField(draft.tagsText, R.string.editor_tags, "editor-tags", 819, enabled) { vm.update(draft.copy(tagsText = it)) }
    if (draft.baseline == null) EditorField(draft.initialText, R.string.editor_initial_text, "editor-initial-text", 10000, enabled, true) { vm.update(draft.copy(initialText = it)) }
    else Text(stringResource(R.string.editor_body_preserved))
    if (!draft.compatible) Text(stringResource(R.string.editor_unsupported), color = MaterialTheme.colorScheme.error)
    else if (!EditorialContract.valid(draft)) Text(stringResource(R.string.editor_invalid), color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("editor-invalid"))
}

@Composable
private fun EditorField(value: String, label: Int, tag: String, limit: Int, enabled: Boolean, multiline: Boolean = false, change: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = change, enabled = enabled, singleLine = !multiline,
        minLines = if (multiline) 3 else 1, label = { Text(stringResource(label), maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingText = { Text("${value.length}/$limit") }, isError = value.length > limit, modifier = Modifier.fillMaxWidth().testTag(tag))
}
