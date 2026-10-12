package com.nullpointer.nullsiteadmin.ui.screens.career

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.StorageException
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.exception.InvalidResumePdf
import com.nullpointer.nullsiteadmin.exception.SkillsImportRequired
import com.nullpointer.nullsiteadmin.domain.career.CareerRepository
import com.nullpointer.nullsiteadmin.exception.NullAppException
import com.nullpointer.nullsiteadmin.models.career.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import javax.inject.Inject

@HiltViewModel
class CareerViewModel @Inject constructor(private val repository: CareerRepository, private val state: SavedStateHandle) : ViewModel() {
    var snapshot by mutableStateOf<CareerSnapshot?>(null); private set
    var busy by mutableStateOf<String?>(null); private set
    var progress by mutableFloatStateOf(0f); private set
    var error by mutableStateOf<Int?>(null); private set
    var notice by mutableStateOf<Int?>(null); private set
    var conflict by mutableStateOf(state.get<Boolean>("career.blocked") ?: false); private set
    private var draftJson by mutableStateOf(state.get<String>("career.draft"))
    val draft: SkillContent? by derivedStateOf { draftJson?.let { CareerContract.json.decodeFromString<SkillContent>(it) } }
    private var originalJson = state.get<String>("career.original")
    var tab by mutableStateOf(state.get<String>("career.tab") ?: "skills"); private set
    var locale by mutableStateOf(state.get<String>("career.locale") ?: "es"); private set
    var pendingUri by mutableStateOf(state.get<String>("career.uri")); private set
    private var resumeBaselineJson = state.get<String>("career.resumeBaseline")
    private var operation: Job? = null
    val hasPendingChanges get() = pendingUri != null || (draftJson != null && draftJson != originalJson)
    val canSaveSkill get() = busy == null && !conflict && snapshot?.skillsManaged == true && draft?.let(CareerContract::valid) == true
    init { launchAction("load") { load() } }

    private fun setDraft(value: SkillContent?) {
        draftJson = value?.let { CareerContract.json.encodeToString(it) }; state["career.draft"] = draftJson
    }
    private fun setResumeBaseline(value: Map<String, ResumeContent>?) {
        resumeBaselineJson = value?.let { CareerContract.json.encodeToString(it) }; state["career.resumeBaseline"] = resumeBaselineJson
    }
    fun changeTab(value: String) { if (busy == null && draft == null && pendingUri == null && value in listOf("skills", "resume")) { tab = value; state["career.tab"] = value } }
    fun changeLocale(value: String) { if (busy == null && pendingUri == null && value in CareerContract.locales) { locale = value; state["career.locale"] = value } }
    fun edit(skill: SkillContent) {
        if (busy != null || conflict || pendingUri != null || snapshot?.skillsManaged != true || draft != null) return
        setDraft(skill); originalJson = draftJson; state["career.original"] = originalJson; error = null
    }
    fun newSkill() { if (draft == null) edit(SkillContent(id = repository.newSkillId())) }
    fun update(value: SkillContent) { if (busy == null && draft != null) setDraft(value.copy(id = draft!!.id, baseline = draft!!.baseline)) }
    fun translation(field: String, value: String) {
        val current = draft ?: return
        update(current.copy(translations = current.translations + (locale to (current.translations[locale].orEmpty() + (field to value)))))
    }
    fun discard() {
        if (busy != null) return
        setDraft(null); originalJson = null; state["career.original"] = null
        pendingUri = null; state["career.uri"] = null; setResumeBaseline(null); conflict = false; state["career.blocked"] = false; error = null
        refresh()
    }
    private suspend fun load() {
        val result = repository.load(); snapshot = result
        // Do not replace the base of a restored selected PDF with fresh server data.
        if (resumeBaselineJson == null) setResumeBaseline(result.resumes)
    }
    fun refresh() {
        if (busy != null || hasPendingChanges || draft != null) return
        conflict = false; state["career.blocked"] = false
        launchAction("load") { setResumeBaseline(null); load() }
    }
    fun importSkills() { if (busy == null && snapshot != null) launchAction("import") { repository.importSkills(); load(); notice = R.string.career_saved } }
    fun saveSkill() {
        if (!canSaveSkill) return
        val value = draft ?: return
        launchAction("save") { repository.saveSkill(value); setDraft(null); originalJson = null; state["career.original"] = null; notice = R.string.career_saved; load() }
    }
    fun deleteSkill() {
        val value = draft ?: return
        if (busy != null || conflict || value.baseline == null) return
        launchAction("delete") { repository.deleteSkill(value); setDraft(null); originalJson = null; state["career.original"] = null; notice = R.string.career_saved; load() }
    }
    fun selectPdf(uri: Uri) {
        if (busy != null || snapshot == null || conflict) return
        pendingUri = uri.toString(); state["career.uri"] = pendingUri; setResumeBaseline(snapshot!!.resumes); error = null
    }
    fun publishResume() {
        if (busy != null || conflict || snapshot == null) return
        val uri = pendingUri?.let(Uri::parse) ?: return
        val selectedLocale = locale
        val baseline = resumeBaselineJson?.let { CareerContract.json.decodeFromString<Map<String, ResumeContent>>(it) }?.get(selectedLocale) ?: ResumeContent()
        progress = 0f
        launchAction("upload") {
            val uploaded = repository.uploadResume(selectedLocale, uri) { progress = it.coerceIn(0f, 100f) }
            busy = "publish" // No cancel button once the transaction can have committed.
            repository.publishResume(selectedLocale, uploaded, baseline)
            pendingUri = null; state["career.uri"] = null; setResumeBaseline(null)
            notice = R.string.career_saved; load()
        }
    }
    fun withdrawResume() {
        if (busy != null || pendingUri != null || conflict) return
        val selectedLocale = locale
        val baseline = snapshot?.resumes?.get(selectedLocale) ?: return
        launchAction("withdraw") { repository.withdrawResume(selectedLocale, baseline); setResumeBaseline(null); notice = R.string.career_saved; load() }
    }
    fun cancelUpload() { if (busy == "upload") operation?.cancel() }
    private fun launchAction(action: String, block: suspend () -> Unit) {
        if (busy != null) return
        busy = action; error = null; notice = null
        operation = viewModelScope.launch {
            try { block() } catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                val contentConflict = e is NullAppException.ContentConflict
                conflict = contentConflict || busy == "publish"
                state["career.blocked"] = conflict
                error = when {
                    contentConflict -> R.string.error_content_conflict
                    e is InvalidResumePdf -> R.string.career_pdf_invalid
                    e is SkillsImportRequired -> R.string.career_import_required
                    e is NullAppException.AdminRequired -> R.string.error_admin_required
                    e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED -> R.string.error_admin_required
                    e is StorageException && e.errorCode == StorageException.ERROR_NOT_AUTHORIZED -> R.string.error_admin_required
                    else -> R.string.career_operation_failed
                }
            } finally { busy = null }
        }
    }
}
