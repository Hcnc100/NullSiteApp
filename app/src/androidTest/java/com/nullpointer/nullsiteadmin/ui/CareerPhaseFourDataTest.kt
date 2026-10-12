package com.nullpointer.nullsiteadmin.ui

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.exception.InvalidResumePdf
import com.nullpointer.nullsiteadmin.domain.career.CareerRepository
import com.nullpointer.nullsiteadmin.models.career.*
import com.nullpointer.nullsiteadmin.ui.screens.career.CareerViewModel
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

internal class CareerQaRepository : CareerRepository {
    var managed = true
    val rawSkills = mutableMapOf<String, Map<String, Any?>>("qa" to CareerContract.skillCreate(SkillContent(id = "qa", name = "Kotlin", published = true)))
    val resumes = mutableMapOf("es" to ResumeContent("resumes/es/old.pdf", "CV-es.pdf", 100, true, "es-old"),
        "en" to ResumeContent("resumes/en/old.pdf", "CV-en.pdf", 101, true, "en-old"))
    var saveCalls = 0; var uploadCalls = 0; var publishCalls = 0; var deleteCalls = 0
    var failUpload = false; var uncertainPublish = false
    var uploadGate: CompletableDeferred<Unit>? = null
    var saveGate: CompletableDeferred<Unit>? = null
    var lastSaved: SkillContent? = null
    override suspend fun load() = CareerSnapshot(rawSkills.map { CareerContract.readSkill(it.key, it.value) }, managed, resumes.toMap())
    override fun newSkillId() = "new-skill"
    override suspend fun importSkills() {
        if (managed) return
        CareerContract.defaultSkills.forEach { rawSkills.putIfAbsent(it.id, CareerContract.skillCreate(it)) }; managed = true
    }
    override suspend fun saveSkill(skill: SkillContent) {
        saveCalls++; saveGate?.await()
        CareerContract.assertSkillBaseline(rawSkills[skill.id], skill.baseline)
        val old = rawSkills[skill.id].orEmpty()
        rawSkills[skill.id] = old + CareerContract.skillCreate(skill)
        lastSaved = skill
    }
    override suspend fun deleteSkill(skill: SkillContent) {
        deleteCalls++; CareerContract.assertSkillBaseline(rawSkills[skill.id], skill.baseline); rawSkills.remove(skill.id)
    }
    override suspend fun uploadResume(locale: String, uri: Uri, progress: (Float) -> Unit): ResumeContent {
        uploadCalls++; progress(40f); uploadGate?.await()
        if (failUpload) throw InvalidResumePdf()
        return ResumeContent("resumes/$locale/new-$uploadCalls.pdf", "CV-$locale.pdf", 200, true)
    }
    override suspend fun publishResume(locale: String, uploaded: ResumeContent, baseline: ResumeContent) {
        publishCalls++
        if (resumes[locale]?.baseline != baseline.baseline) throw com.nullpointer.nullsiteadmin.exception.NullAppException.ContentConflict
        resumes[locale] = uploaded.copy(baseline = "new-$publishCalls")
        if (uncertainPublish) throw IllegalStateException("commit acknowledgement lost")
    }
    override suspend fun withdrawResume(locale: String, baseline: ResumeContent) {
        if (resumes[locale]?.baseline != baseline.baseline) throw com.nullpointer.nullsiteadmin.exception.NullAppException.ContentConflict
        resumes[locale] = baseline.copy(enabled = false, baseline = "withdrawn")
    }
}

class CareerPhaseFourDataTest {
    private suspend fun main(block: () -> Unit) = withContext(Dispatchers.Main) { block() }
    private suspend fun idle(vm: CareerViewModel) { withTimeout(5000) { while (vm.busy != null) delay(10) } }
    private suspend fun create(repo: CareerQaRepository, state: SavedStateHandle = SavedStateHandle()): CareerViewModel {
        lateinit var vm: CareerViewModel; main { vm = CareerViewModel(repo, state) }; idle(vm); return vm
    }
    @Test fun restoredSkillKeepsOriginalBaselineAndDetectsRemoteEditAndDelete() = runBlocking {
        val repo = CareerQaRepository(); val state = SavedStateHandle(); val vm = create(repo, state)
        main { vm.edit(vm.snapshot!!.skills.single()); vm.update(vm.draft!!.copy(name = "My draft", translations = mapOf("en" to mapOf("name" to "EN")))) }
        val original = vm.draft!!.baseline
        repo.rawSkills["qa"] = repo.rawSkills["qa"]!! + ("name" to "Remote")
        val restored = create(repo, SavedStateHandle(state.keys().associateWith { state.get<Any?>(it) }))
        main { assertEquals("My draft", restored.draft!!.name); assertEquals(original, restored.draft!!.baseline); restored.saveSkill() }
        idle(restored)
        main { assertEquals(R.string.error_content_conflict, restored.error); assertEquals("My draft", restored.draft!!.name); assertTrue(restored.conflict); assertEquals("Remote", repo.rawSkills["qa"]!!["name"]); restored.discard() }
        idle(restored); main { restored.edit(restored.snapshot!!.skills.single()) }
        repo.rawSkills.clear(); main { restored.deleteSkill() }; idle(restored)
        assertEquals(R.string.error_content_conflict, restored.error)
    }
    @Test fun cancelledUploadKeepsSelectionAndNeverActivatesPartialFile() = runBlocking {
        val repo = CareerQaRepository().apply { uploadGate = CompletableDeferred() }; val vm = create(repo)
        main { vm.selectPdf(Uri.parse("content://qa/cv.pdf")); vm.publishResume(); vm.publishResume(); vm.changeLocale("en") }
        assertEquals(1, repo.uploadCalls); assertEquals("es", vm.locale); assertEquals(40f, vm.progress)
        assertEquals("resumes/es/old.pdf", repo.resumes["es"]!!.storagePath)
        main { vm.cancelUpload() }; idle(vm)
        assertNotNull(vm.pendingUri); assertEquals(0, repo.publishCalls); assertTrue(repo.resumes["es"]!!.enabled)
    }
    @Test fun invalidPdfCanRetryAndReplacementOnlyChangesSelectedLocale() = runBlocking {
        val repo = CareerQaRepository().apply { failUpload = true }; val vm = create(repo)
        main { vm.selectPdf(Uri.parse("content://qa/cv.pdf")); vm.publishResume() }; idle(vm)
        assertEquals(R.string.career_pdf_invalid, vm.error); assertNotNull(vm.pendingUri); assertEquals(0, repo.publishCalls)
        repo.failUpload = false; main { vm.publishResume() }; idle(vm)
        assertNull(vm.pendingUri); assertEquals(1, repo.publishCalls); assertTrue(repo.resumes["es"]!!.storagePath.contains("new-"))
        assertEquals("resumes/en/old.pdf", repo.resumes["en"]!!.storagePath)
    }
    @Test fun restoredPdfDoesNotAdoptFreshRemoteBaseline() = runBlocking {
        val repo = CareerQaRepository(); val state = SavedStateHandle(); val vm = create(repo, state)
        main { vm.selectPdf(Uri.parse("content://qa/cv.pdf")) }
        repo.resumes["es"] = repo.resumes["es"]!!.copy(storagePath = "resumes/es/remote.pdf", baseline = "remote")
        val restored = create(repo, SavedStateHandle(state.keys().associateWith { state.get<Any?>(it) }))
        main { restored.publishResume() }; idle(restored)
        assertEquals(R.string.error_content_conflict, restored.error); assertNotNull(restored.pendingUri)
        assertEquals("resumes/es/remote.pdf", repo.resumes["es"]!!.storagePath)
    }
    @Test fun ambiguousCommitBlocksBlindRetryAndSurvivesRestoration() = runBlocking {
        val repo = CareerQaRepository().apply { uncertainPublish = true }; val state = SavedStateHandle(); val vm = create(repo, state)
        main { vm.selectPdf(Uri.parse("content://qa/cv.pdf")); vm.publishResume() }; idle(vm)
        assertEquals(R.string.career_operation_failed, vm.error); assertTrue(vm.conflict); assertNotNull(vm.pendingUri)
        assertTrue(repo.resumes["es"]!!.storagePath.contains("new-"))
        val restored = create(repo, SavedStateHandle(state.keys().associateWith { state.get<Any?>(it) }))
        main { restored.changeTab("skills"); restored.edit(restored.snapshot!!.skills.single()); restored.publishResume() }
        assertNull(restored.draft); assertEquals(1, repo.publishCalls); assertTrue(restored.conflict)
        main { restored.discard() }; idle(restored); assertFalse(restored.conflict); assertNull(restored.pendingUri)
        assertTrue(restored.snapshot!!.resumes["es"]!!.enabled)
    }
    @Test fun singleFlightSaveAndExplicitDelete() = runBlocking {
        val repo = CareerQaRepository().apply { saveGate = CompletableDeferred() }; val vm = create(repo)
        main { vm.edit(vm.snapshot!!.skills.single()); vm.update(vm.draft!!.copy(name = "Edited")); vm.saveSkill(); vm.saveSkill() }
        assertEquals(1, repo.saveCalls); repo.saveGate!!.complete(Unit); idle(vm)
        assertNull(vm.draft); assertEquals("Edited", vm.snapshot!!.skills.single().name)
        main { vm.edit(vm.snapshot!!.skills.single()); vm.deleteSkill() }; idle(vm)
        assertEquals(1, repo.deleteCalls); assertTrue(vm.snapshot!!.skills.isEmpty())
    }
    @Test fun initialImportIsExplicitPreservesExistingAndDoesNotResurrectDeletedDefaults() = runBlocking {
        val repo = CareerQaRepository().apply { managed = false; rawSkills["python"] = CareerContract.skillCreate(SkillContent(id = "python", name = "Custom Python")) }
        val vm = create(repo); main { vm.newSkill() }; assertNull(vm.draft)
        main { vm.importSkills() }; idle(vm)
        assertTrue(repo.managed); assertEquals("Custom Python", repo.rawSkills["python"]!!["name"])
        repo.rawSkills.remove("java"); main { vm.importSkills() }; idle(vm); assertFalse(repo.rawSkills.containsKey("java"))
    }
    @Test fun withdrawalDisablesOnlyLocaleAndKeepsPath() = runBlocking {
        val repo = CareerQaRepository(); val vm = create(repo)
        main { vm.withdrawResume() }; idle(vm)
        assertFalse(repo.resumes["es"]!!.enabled); assertEquals("resumes/es/old.pdf", repo.resumes["es"]!!.storagePath)
        assertTrue(repo.resumes["en"]!!.enabled)
    }
    @Test fun newSkillRequiresValidFieldsAndStartsUnpublished() = runBlocking {
        val repo = CareerQaRepository(); val vm = create(repo)
        main { vm.newSkill(); vm.saveSkill() }; assertEquals(0, repo.saveCalls)
        main { vm.update(vm.draft!!.copy(name = "Docker", iconKey = "docker", category = "tech", color = "#0db7ed", sortOrder = "2")); vm.saveSkill() }
        idle(vm); assertNull(vm.draft); assertEquals(1, repo.saveCalls)
        assertEquals(false, repo.rawSkills["new-skill"]!!["published"]); assertEquals("Docker", repo.rawSkills["new-skill"]!!["name"])
    }
    @Test fun englishPdfPublishesAndWithdrawsWithoutChangingSpanishPointer() = runBlocking {
        val repo = CareerQaRepository(); val vm = create(repo)
        main { vm.changeLocale("en"); vm.selectPdf(Uri.parse("content://qa/cv-en.pdf")); vm.publishResume() }; idle(vm)
        assertTrue(repo.resumes["en"]!!.storagePath.startsWith("resumes/en/new-"))
        assertEquals("CV-en.pdf", repo.resumes["en"]!!.fileName)
        assertEquals("resumes/es/old.pdf", repo.resumes["es"]!!.storagePath)
        main { vm.withdrawResume() }; idle(vm)
        assertFalse(repo.resumes["en"]!!.enabled); assertTrue(repo.resumes["es"]!!.enabled)
    }
}
