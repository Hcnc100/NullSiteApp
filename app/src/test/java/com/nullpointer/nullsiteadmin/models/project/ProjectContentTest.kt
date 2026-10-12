package com.nullpointer.nullsiteadmin.models.project

import androidx.lifecycle.SavedStateHandle
import com.nullpointer.nullsiteadmin.core.utils.ContentEditGuard
import com.nullpointer.nullsiteadmin.exception.NullAppException
import com.nullpointer.nullsiteadmin.models.project.data.*
import com.nullpointer.nullsiteadmin.models.project.entity.ProjectEntity
import com.nullpointer.nullsiteadmin.models.project.dto.UpdateProjectDTO
import com.nullpointer.nullsiteadmin.models.project.wrapper.UpdateProjectWrapper
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.viewModel.ProjectContentEditorState
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class ProjectContentTest {
    private val a = ProjectImage("https://example.com/a.png", "A", translations = mapOf("en" to mapOf("alt" to "English A")),
        extra = mapOf("future" to JsonPrimitive("preserved")))
    private val b = ProjectImage("https://example.com/b.png", "B", order = 1)
    private val content = ProjectContent(urlPlay = "https://example.com/demo", links = listOf(ProjectLink("Docs", "https://example.com/docs")),
        technologyIds = listOf("kotlin"), gallery = listOf(a.url, b.url), galleryItems = listOf(a, b),
        translations = mapOf("fr" to mapOf("name" to "Français")))

    @Test fun legacyCacheDecodesWithEmptyContent() { assertEquals(ProjectContent(), ProjectContentContract.decode("{}")) }
    @Test fun enrichedCacheAndEntityRoundTrip() {
        val project = ProjectData(idProject = "id", createdAt = Date(1), updatedAt = Date(2), content = content)
        assertEquals(project, ProjectData.fromProjectEntity(ProjectEntity.fromProjectData(project)))
    }
    @Test fun galleryMovePreservesMetadataAndNormalizesBothOrders() {
        val state = ProjectContentEditorState(SavedStateHandle()); state.initialize(content); state.moveImage(0, 1)
        assertEquals(listOf(b.url, a.url), state.content.gallery)
        assertEquals("A", state.images[1].alt)
        assertEquals("English A", state.images[1].translations["en"]?.get("alt"))
        assertEquals(a.extra, state.images[1].extra)
        assertEquals(listOf(0L, 1L), state.images.map { it.order })
    }
    @Test fun legacyGalleryCreatesMetadataWithoutLosingUrls() {
        assertEquals(listOf(a.url, b.url), ProjectContentContract.effectiveGallery(ProjectContent(gallery = listOf(a.url,b.url))).map { it.url })
        assertEquals(listOf(a.url,b.url), ProjectContentContract.effectiveGallery(ProjectContent(galleryItems = listOf(b,a))).map { it.url })
    }
    @Test fun removeKeepsRemainingMetadata() {
        val state = ProjectContentEditorState(SavedStateHandle()); state.initialize(content); state.removeImage(0)
        assertEquals(listOf(b.url), state.content.gallery); assertEquals("B", state.images.single().alt)
    }
    @Test fun changedOnlyPatchKeepsUneditedPublicationAndUnknownLocalesOut() {
        val state = ProjectContentEditorState(SavedStateHandle()); state.initialize(content); state.changeTranslation("name", "Nombre")
        val project = ProjectData(idProject = "id", content = content)
        val patch = UpdateProjectDTO.fromUpdateProjectWrapper(UpdateProjectWrapper("id", null,null,null,null,null,project,state.content)).toUpdateMap()
        assertEquals("Nombre", patch["translations.es.name"])
        assertFalse(patch.containsKey("translations")); assertFalse(patch.containsKey("published"))
        assertFalse(patch.containsKey("gallery")); assertFalse(patch.containsKey("originalContent")); assertFalse(patch.containsKey("content"))
    }
    @Test fun urlsRejectHttpCredentialsAndOversize() {
        assertFalse(ProjectContentContract.valid(content.copy(urlPlay = "http://example.com")))
        assertFalse(ProjectContentContract.valid(content.copy(thumbnailUrl = "https://u:p@example.com")))
        assertFalse(ProjectContentContract.valid(content.copy(urlPlay = "https://example.com/" + "a".repeat(2048))))
        assertTrue(ProjectContentContract.valid(content))
    }
    @Test fun altAndTranslationBoundariesAreValidated() {
        assertTrue(ProjectContentContract.valid(content.copy(coverAlt = "a".repeat(300))))
        assertFalse(ProjectContentContract.valid(content.copy(coverAlt = "a".repeat(301))))
        assertFalse(ProjectContentContract.valid(content.copy(galleryItems = listOf(a.copy(translations = mapOf("en" to mapOf("alt" to "a".repeat(301))))))))
    }
    @Test fun galleryLimitAndInvalidLinksBlockSave() {
        assertFalse(ProjectContentContract.valid(content.copy(gallery = List(61) { a.url })))
        assertFalse(ProjectContentContract.valid(content.copy(links = listOf(ProjectLink("", a.url)))))
    }
    @Test fun webCombinedUrlAndCollectionLimitsMatch() {
        val gallery = List(57) { "https://example.com/$it.png" }
        assertTrue(ProjectContentContract.valid(content.copy(gallery = gallery, galleryItems = null)))
        assertFalse(ProjectContentContract.valid(content.copy(gallery = gallery + "https://example.com/58.png", galleryItems = null)))
        assertFalse(ProjectContentContract.valid(content.copy(technologyIds = List(31) { "tech$it" })))
        assertFalse(ProjectContentContract.valid(content.copy(links = List(11) { ProjectLink("Docs",a.url) })))
    }
    @Test fun createMapUsesNestedTranslationsAndNoInternalImageFields() {
        val map = ProjectContentContract.create(content)
        assertTrue(map.containsKey("translations"))
        assertFalse(map.keys.any { it.startsWith("translations.") })
        val image = (map["galleryItems"] as List<*>).first() as Map<*, *>
        assertFalse(image.containsKey("extra")); assertFalse(image.containsKey("presentFields"))
        assertEquals("preserved",image["future"])
    }
    @Test fun originalSparseWebMetadataDoesNotCauseFalseConflict() {
        val raw = mapOf("galleryItems" to listOf(mapOf("url" to a.url, "order" to 0L, "future" to "keep")))
        val project = ProjectData(content = ProjectContentContract.read(raw))
        ContentEditGuard.requireUnchanged(raw, ContentEditGuard.projectBaseline(project))
        assertEquals("keep", project.content.galleryItems!!.single().extra["future"]?.let { (it as JsonPrimitive).content })
    }
    @Test fun remoteGalleryAndTranslationChangesAreConflicts() {
        val project = ProjectData(content = content)
        val raw = ProjectContentContract.create(content)
        assertThrows(NullAppException.ContentConflict::class.java) {
            ContentEditGuard.requireUnchanged(raw + ("gallery" to emptyList<String>()), ContentEditGuard.projectBaseline(project))
        }
        assertThrows(NullAppException.ContentConflict::class.java) {
            ContentEditGuard.requireUnchanged(raw + ("translations" to mapOf("en" to mapOf("name" to "changed"))), ContentEditGuard.projectBaseline(project))
        }
    }
    @Test fun restoredDraftKeepsBothLanguagesAndRawTechnologiesInput() {
        val saved = SavedStateHandle(); val first = ProjectContentEditorState(saved); first.initialize(content)
        first.changeTechnologies("kotlin, "); first.changeTranslation("name", "Español"); first.selectLocale("en"); first.changeTranslation("name", "English")
        val restored = ProjectContentEditorState(SavedStateHandle(saved.keys().associateWith { saved.get<Any?>(it) }))
        restored.initialize(ProjectContent())
        assertEquals("kotlin, ", restored.technologiesText); assertEquals("English", restored.translation("name"))
        restored.selectLocale("es"); assertEquals("Español", restored.translation("name"))
    }
    @Test fun invalidOrderRemainsVisibleAndInvalidAfterRestore() {
        val saved = SavedStateHandle(); val state = ProjectContentEditorState(saved); state.initialize(content); state.changeOrder("not-an-integer")
        assertFalse(state.isValid)
        assertFalse(ProjectContentEditorState(SavedStateHandle(saved.keys().associateWith { saved.get<Any?>(it) })).isValid)
    }
}
