package com.nullpointer.nullsiteadmin.ui.screens.editor

import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.domain.editor.EditorialRepository
import com.nullpointer.nullsiteadmin.exception.*
import com.nullpointer.nullsiteadmin.models.career.CareerContract
import com.nullpointer.nullsiteadmin.models.editor.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import javax.inject.Inject

@HiltViewModel
class EditorialViewModel @Inject constructor(private val repository: EditorialRepository, private val state: SavedStateHandle) : ViewModel() {
    var snapshot by mutableStateOf<EditorialSnapshot?>(null); private set
    var busy by mutableStateOf(false); private set
    var error by mutableStateOf<Int?>(null); private set
    var saved by mutableStateOf(false); private set
    var blocked by mutableStateOf(state.get<Boolean>("editor.blocked") ?: false); private set
    var section by mutableStateOf(state.get<String>("editor.section") ?: "blog"); private set
    private var draftJson by mutableStateOf(state.get<String>("editor.draft"))
    val draft: BlogMetadata? by derivedStateOf { draftJson?.let { CareerContract.json.decodeFromString<BlogMetadata>(it) } }
    var history by mutableStateOf<List<LegalRevision>?>(null); private set
    var historyId by mutableStateOf<String?>(null); private set
    val canSave get() = !busy && !blocked && draft?.let(EditorialContract::valid) == true && snapshot != null
    init { action { snapshot = repository.load() } }
    private fun setDraft(value: BlogMetadata?) { draftJson = value?.let { CareerContract.json.encodeToString(it) }; state["editor.draft"] = draftJson }
    fun section(value: String) { if (!busy && draft == null && value in listOf("blog","legal")) { section = value; state["editor.section"] = value } }
    fun edit(value: BlogMetadata) { if (!busy && draft == null && !blocked) { setDraft(value); saved = false; error = null } }
    fun create() { if (!busy && snapshot != null && !blocked && draft == null) edit(BlogMetadata(id = repository.newId())) }
    fun update(value: BlogMetadata) { if (!busy && draft != null) setDraft(value.copy(id = draft!!.id, baseline = draft!!.baseline,
        revision = draft!!.revision, publicationKey = draft!!.publicationKey, publishedRevision = draft!!.publishedRevision, compatible = draft!!.compatible)) }
    fun refresh() { if (!busy && draft == null) action { snapshot = repository.load(); blocked = false; state["editor.blocked"] = false; history = null; historyId = null } }
    fun discard() { if (!busy) { setDraft(null); blocked = false; state["editor.blocked"] = false; refresh() } }
    fun save() {
        if (!canSave) return
        val value = draft ?: return
        action { repository.saveMetadata(value); setDraft(null); saved = true; snapshot = repository.load() }
    }
    fun history(id: String) { if (!busy) action { history = repository.history(id); historyId = id } }
    fun browserFailed() { error = R.string.editor_browser_error }
    private fun action(block: suspend () -> Unit) {
        if (busy) return
        busy = true; error = null
        viewModelScope.launch {
            try { block() } catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                if (e is NullAppException.ContentConflict) { blocked = true; state["editor.blocked"] = true }
                error = when {
                    e is NullAppException.ContentConflict -> R.string.error_content_conflict
                    e is BlogSlugTaken -> R.string.editor_slug_taken
                    e is UnsupportedBlogDraft -> R.string.editor_unsupported
                    e is NullAppException.AdminRequired || e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED -> R.string.error_admin_required
                    else -> R.string.editor_operation_failed
                }
            } finally { busy = false }
        }
    }
}
