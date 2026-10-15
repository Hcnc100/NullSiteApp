package com.nullpointer.nullsiteadmin.ui.screens.career

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.career.*
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.share.AppPullToRefreshBox
import com.nullpointer.nullsiteadmin.ui.share.rememberAppRefreshState
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph
@Destination
@Composable
fun CareerScreen(viewModel: CareerViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            viewModel.selectPdf(uri)
        }
    }
    CareerContent(viewModel) { picker.launch(arrayOf("application/pdf")) }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun CareerContent(vm: CareerViewModel, selectPdf: () -> Unit) {
    var confirmation by remember { mutableStateOf<String?>(null) }
    val enabled = vm.busy == null
    val canChangeTab = enabled && vm.draft == null && vm.pendingUri == null
    val refreshState = rememberAppRefreshState(vm.busy == "load", vm::refresh)
    BackHandler(vm.busy != null || vm.draft != null || vm.pendingUri != null) {
        if (enabled) confirmation = "discard"
    }
    Surface(Modifier.fillMaxSize()) {
    AppPullToRefreshBox(refreshState, Modifier.fillMaxSize().testTag("career-refresh-container"),
        enabled = vm.draft == null && vm.pendingUri == null && (enabled || vm.busy == "load")) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.career_title), style = MaterialTheme.typography.headlineSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(selected = vm.tab == "skills", onClick = { vm.changeTab("skills") }, enabled = canChangeTab,
                label = { Text(stringResource(R.string.career_skills)) }, modifier = Modifier.testTag("career-tab-skills"))
            FilterChip(selected = vm.tab == "resume", onClick = { vm.changeTab("resume") }, enabled = canChangeTab,
                label = { Text(stringResource(R.string.career_resume)) }, modifier = Modifier.testTag("career-tab-resume"))
        }
        vm.error?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("career-error")) }
        vm.notice?.let { Text(stringResource(it), modifier = Modifier.testTag("career-notice")) }
        if (vm.busy != null && vm.busy != "load") {
            if (vm.busy == "upload") {
                LinearProgressIndicator(progress = { vm.progress / 100f }, modifier = Modifier.fillMaxWidth())
                Text(stringResource(R.string.career_upload_progress, vm.progress.toInt()))
                OutlinedButton(onClick = vm::cancelUpload, modifier = Modifier.testTag("career-cancel-upload")) { Text(stringResource(R.string.project_cancel_upload)) }
            } else { LinearProgressIndicator(Modifier.fillMaxWidth()); Text(stringResource(R.string.career_working)) }
        }
        if (vm.snapshot == null) {
            if (enabled && vm.error != null) OutlinedButton(onClick = vm::refresh,
                modifier = Modifier.testTag("career-retry")) { Text(stringResource(R.string.career_reload)) }
        } else if (vm.tab == "skills") {
            if (vm.snapshot?.skillsManaged != true) {
                Text(stringResource(R.string.career_import_required))
                Button(onClick = { confirmation = "import" }, enabled = enabled, modifier = Modifier.testTag("career-import")) { Text(stringResource(R.string.career_import)) }
            }
            val skill = vm.draft
            if (skill == null) {
                Button(onClick = vm::newSkill, enabled = enabled && !vm.conflict && vm.snapshot?.skillsManaged == true, modifier = Modifier.testTag("career-new")) { Text(stringResource(R.string.career_new)) }
                if (vm.snapshot!!.skills.isEmpty()) Text(stringResource(R.string.career_empty))
                vm.snapshot!!.skills.forEach { item ->
                    OutlinedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(item.name, style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(if (item.published) R.string.career_published else R.string.career_unpublished))
                            Text("${item.iconKey} · ${item.color} · ${item.sortOrder}")
                            TextButton(onClick = { vm.edit(item) }, enabled = enabled && !vm.conflict && vm.snapshot?.skillsManaged == true,
                                modifier = Modifier.testTag("career-edit-${item.id}")) { Text(stringResource(R.string.career_edit)) }
                        }
                    }
                }
            } else {
                SkillEditor(vm, skill, enabled)
                Button(onClick = vm::saveSkill, enabled = vm.canSaveSkill, modifier = Modifier.testTag("career-save")) { Text(stringResource(R.string.career_save)) }
                if (skill.baseline != null) OutlinedButton(onClick = { confirmation = "delete" }, enabled = enabled && !vm.conflict,
                    modifier = Modifier.testTag("career-delete")) { Text(stringResource(R.string.career_delete)) }
                OutlinedButton(onClick = { confirmation = "discard" }, enabled = enabled, modifier = Modifier.testTag("career-discard")) { Text(stringResource(R.string.career_close)) }
            }
        } else {
            LocalePicker(vm, enabled && vm.pendingUri == null)
            val resume = vm.snapshot!!.resumes[vm.locale] ?: ResumeContent()
            Text(stringResource(if (CareerContract.activeResume(vm.locale, resume)) R.string.career_resume_active else R.string.career_resume_inactive),
                style = MaterialTheme.typography.titleMedium, modifier = Modifier.testTag("career-resume-status"))
            if (resume.fileName.isNotBlank()) Text("${resume.fileName} · ${resume.sizeBytes} B")
            Text(stringResource(R.string.career_resume_help))
            OutlinedButton(onClick = selectPdf, enabled = enabled && !vm.conflict, modifier = Modifier.testTag("career-select-pdf")) { Text(stringResource(R.string.career_select_pdf)) }
            if (vm.pendingUri != null) {
                Text(stringResource(R.string.career_pdf_selected), modifier = Modifier.testTag("career-pdf-pending"))
                Button(onClick = vm::publishResume, enabled = enabled && !vm.conflict, modifier = Modifier.testTag("career-publish")) { Text(stringResource(R.string.career_publish)) }
                TextButton(onClick = { confirmation = "discard" }, enabled = enabled) { Text(stringResource(R.string.career_close)) }
            }
            OutlinedButton(onClick = { confirmation = "withdraw" }, enabled = enabled && !vm.conflict && vm.pendingUri == null && resume.enabled,
                modifier = Modifier.testTag("career-withdraw")) { Text(stringResource(R.string.career_withdraw)) }
        }
    }
    }
    }
    confirmation?.let { action ->
        val title = stringResource(when (action) {
            "delete" -> R.string.career_confirm_delete
            "withdraw" -> R.string.career_confirm_withdraw
            "import" -> R.string.career_confirm_import
            else -> R.string.project_discard_title
        })
        val explanation = stringResource(if (action == "withdraw") R.string.career_withdraw_help else R.string.career_confirm_help)
        val confirmLabel = stringResource(R.string.career_confirm)
        val cancelLabel = stringResource(R.string.profile_keep_editing)
        AlertDialog(onDismissRequest = { confirmation = null }, title = { Text(title) },
            text = { Column(Modifier.testTag("career-dialog-body").verticalScroll(rememberScrollState())) { Text(explanation) } },
            confirmButton = { TextButton(onClick = {
                confirmation = null
                when (action) { "delete" -> vm.deleteSkill(); "withdraw" -> vm.withdrawResume(); "import" -> vm.importSkills(); else -> vm.discard() }
            }, modifier = Modifier.testTag("career-confirm")) { Text(confirmLabel) } },
            dismissButton = { TextButton(onClick = { confirmation = null }) { Text(cancelLabel) } })
    }
}

@Composable
private fun SkillEditor(vm: CareerViewModel, skill: SkillContent, enabled: Boolean) {
    CareerField(skill.name, R.string.career_name, "skill-name", 120, enabled) { vm.update(skill.copy(name = it)) }
    CareerField(skill.description, R.string.career_description, "skill-description", 10000, enabled, multiline = true) { vm.update(skill.copy(description = it)) }
    Column {
        Text(stringResource(R.string.career_category))
        listOf("language" to R.string.career_language, "tech" to R.string.career_tech).forEach { (key, label) ->
            FilterChip(selected = skill.category == key, onClick = { vm.update(skill.copy(category = key)) }, enabled = enabled,
                label = { Text(stringResource(label)) }, modifier = Modifier.testTag("skill-category-$key"))
        }
    }
    var menu by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { menu = true }, enabled = enabled, modifier = Modifier.testTag("skill-icon")) {
            Text("${stringResource(R.string.career_icon)}: ${skill.iconKey}")
        }
        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
            CareerContract.icons.forEach { key -> DropdownMenuItem(text = { Text(key) }, onClick = { vm.update(skill.copy(iconKey = key)); menu = false }) }
        }
    }
    CareerField(skill.color, R.string.career_color, "skill-color", 7, enabled) { vm.update(skill.copy(color = it)) }
    if (Regex("^#[a-fA-F0-9]{6}$").matches(skill.color)) Box(Modifier.size(48.dp).background(Color(android.graphics.Color.parseColor(skill.color))))
    CareerField(skill.sortOrder, R.string.career_order, "skill-order", 4, enabled) { vm.update(skill.copy(sortOrder = it)) }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        val publishedLabel = stringResource(R.string.career_published)
        Text(publishedLabel, modifier = Modifier.weight(1f))
        Switch(checked = skill.published, onCheckedChange = { vm.update(skill.copy(published = it)) }, enabled = enabled,
            modifier = Modifier.testTag("skill-published").semantics { contentDescription = publishedLabel })
    }
    LocalePicker(vm, enabled && vm.pendingUri == null)
    CareerField(skill.translations[vm.locale]?.get("name").orEmpty(), R.string.career_translated_name, "skill-translated-name", 120, enabled) { vm.translation("name", it) }
    CareerField(skill.translations[vm.locale]?.get("description").orEmpty(), R.string.career_translated_description, "skill-translated-description", 10000, enabled, multiline = true) { vm.translation("description", it) }
    if (!CareerContract.valid(skill)) Text(stringResource(R.string.career_skill_invalid), color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("career-skill-invalid"))
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun LocalePicker(vm: CareerViewModel, enabled: Boolean) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CareerContract.locales.forEach { locale -> FilterChip(selected = vm.locale == locale, enabled = enabled,
            onClick = { vm.changeLocale(locale) }, label = { Text(if (locale == "es") "Español" else "English") }, modifier = Modifier.testTag("career-locale-$locale")) }
    }
}

@Composable
private fun CareerField(value: String, label: Int, tag: String, max: Int, enabled: Boolean, multiline: Boolean = false, change: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = change, enabled = enabled, singleLine = !multiline, minLines = if (multiline) 3 else 1,
        label = { Text(stringResource(label), maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingText = { Text("${value.length}/$max") }, isError = value.length > max,
        modifier = Modifier.fillMaxWidth().testTag(tag))
}
