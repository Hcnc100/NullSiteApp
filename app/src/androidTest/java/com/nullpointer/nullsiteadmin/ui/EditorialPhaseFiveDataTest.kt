package com.nullpointer.nullsiteadmin.ui

import androidx.lifecycle.SavedStateHandle
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.domain.editor.EditorialRepository
import com.nullpointer.nullsiteadmin.exception.BlogSlugTaken
import com.nullpointer.nullsiteadmin.models.editor.*
import com.nullpointer.nullsiteadmin.ui.screens.editor.EditorialViewModel
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

internal class EditorialQaRepository : EditorialRepository {
    var raw = mapOf<String,Any?>("title" to "Article QA", "slug" to "article-qa", "excerpt" to "Summary", "locale" to "es", "tags" to listOf("Kotlin"),
        "contentHtml" to "<p>Private <strong>rich body</strong><img src=\"blog-drafts/qa/a.png\"></p>", "contentFormat" to "html", "contentVersion" to 1L,
        "status" to "draft", "revision" to 2L, "publicationKey" to "es-article-qa", "publishedRevision" to 1L, "future" to "keep")
    var present = true; var saves = 0; var slugTaken = false; var gate: CompletableDeferred<Unit>? = null
    var lastSaved: BlogMetadata? = null; var failLoad = false
    override fun newId() = "new-draft"
    override suspend fun load(): EditorialSnapshot {
        if (failLoad) throw IllegalStateException("offline")
        return EditorialSnapshot(if (present) listOf(EditorialContract.readBlog("qa",raw)) else emptyList(),
            listOf(LegalMetadata("privacy", "Privacy QA", true, true, 2, 1)), false, false)
    }
    override suspend fun saveMetadata(value: BlogMetadata) {
        saves++; gate?.await()
        if (slugTaken) throw BlogSlugTaken()
        EditorialContract.assertBaseline(if (value.id == "qa" && present) raw else null,value.baseline)
        raw = if (value.baseline == null) EditorialContract.patch(value) + mapOf("contentHtml" to EditorialContract.initialHtml(value.initialText)) else raw + EditorialContract.patch(value)
        lastSaved = value
    }
    override suspend fun history(id: String) = listOf(LegalRevision("v1","Privacy QA",1))
}

class EditorialPhaseFiveDataTest {
    private suspend fun main(block: () -> Unit) = withContext(Dispatchers.Main) { block() }
    private suspend fun idle(vm: EditorialViewModel) { withTimeout(5000) { while (vm.busy) delay(10) } }
    private suspend fun create(repo: EditorialQaRepository, state: SavedStateHandle = SavedStateHandle()): EditorialViewModel {
        lateinit var vm: EditorialViewModel; main { vm = EditorialViewModel(repo,state) }; idle(vm); return vm
    }
    @Test fun nativeMetadataKeepsRichBodyPublicSnapshotAndUnknownFields() = runBlocking {
        val repo = EditorialQaRepository(); val vm = create(repo); val body = repo.raw["contentHtml"]
        main { vm.edit(vm.snapshot!!.blogs.single()); vm.update(vm.draft!!.copy(title = "New title", locale = "en", slug = "new-title")); vm.save() }; idle(vm)
        assertEquals(body,repo.raw["contentHtml"]); assertEquals("es-article-qa",repo.raw["publicationKey"]); assertEquals(1L,repo.raw["publishedRevision"])
        assertEquals("keep",repo.raw["future"]); assertEquals(3L,repo.raw["revision"]); assertNull(vm.draft)
    }
    @Test fun restoredMetadataRejectsRemoteBodyChangeWithoutLosingDraft() = runBlocking {
        val repo = EditorialQaRepository(); val state = SavedStateHandle(); val vm = create(repo,state)
        main { vm.edit(vm.snapshot!!.blogs.single()); vm.update(vm.draft!!.copy(title = "My draft")) }
        val baseline = vm.draft!!.baseline; repo.raw = repo.raw + ("contentHtml" to "<p>Remote body</p>")
        val restored = create(repo,SavedStateHandle(state.keys().associateWith { state.get<Any?>(it) }))
        main { assertEquals(baseline,restored.draft!!.baseline); restored.save() }; idle(restored)
        assertEquals("My draft",restored.draft!!.title); assertEquals(R.string.error_content_conflict,restored.error); assertTrue(restored.blocked)
        main { restored.save() }; assertEquals(1,repo.saves)
    }
    @Test fun deletedDraftIsNeverRecreatedByMetadataSave() = runBlocking {
        val repo = EditorialQaRepository(); val vm = create(repo)
        main { vm.edit(vm.snapshot!!.blogs.single()) }; repo.present = false
        main { vm.save() }; idle(vm); assertEquals(R.string.error_content_conflict,vm.error); assertFalse(repo.present)
    }
    @Test fun slugCollisionKeepsInputAndAllowsCorrection() = runBlocking {
        val repo = EditorialQaRepository().apply { slugTaken = true }; val vm = create(repo)
        main { vm.edit(vm.snapshot!!.blogs.single()); vm.save() }; idle(vm)
        assertEquals(R.string.editor_slug_taken,vm.error); assertNotNull(vm.draft); assertFalse(vm.blocked)
        repo.slugTaken = false; main { vm.update(vm.draft!!.copy(slug = "free-slug")); vm.save() }; idle(vm); assertNull(vm.draft)
    }
    @Test fun singleFlightAndCannotChangeSectionDuringEditing() = runBlocking {
        val repo = EditorialQaRepository().apply { gate = CompletableDeferred() }; val vm = create(repo)
        main { vm.edit(vm.snapshot!!.blogs.single()); vm.section("legal"); vm.save(); vm.save() }
        assertEquals("blog",vm.section); assertEquals(1,repo.saves)
        repo.gate!!.complete(Unit); idle(vm); assertTrue(vm.saved)
    }
    @Test fun plainDraftRequiresBodyAndDoesNotPublish() = runBlocking {
        val repo = EditorialQaRepository(); val vm = create(repo)
        main { vm.create(); vm.update(vm.draft!!.copy(title = "New", slug = "new")); vm.save() }; assertEquals(0,repo.saves)
        main { vm.update(vm.draft!!.copy(initialText = "<script>not executable</script>")); vm.save() }; idle(vm)
        assertFalse((repo.raw["contentHtml"] as String).contains("<script>")); assertFalse(repo.raw.containsKey("publicationKey")); assertEquals(1L,repo.raw["revision"])
    }
    @Test fun historyOnlyReadsAndLoadFailureCanRetry() = runBlocking {
        val repo = EditorialQaRepository().apply { failLoad = true }; val vm = create(repo)
        assertNull(vm.snapshot); assertNotNull(vm.error); repo.failLoad = false
        main { vm.refresh() }; idle(vm); main { vm.section("legal"); vm.history("privacy") }; idle(vm)
        assertEquals("privacy",vm.historyId); assertEquals(1L,vm.history!!.single().revision); assertEquals(0,repo.saves)
    }
}
