package com.nullpointer.nullsiteadmin.models.editor

import com.nullpointer.nullsiteadmin.exception.NullAppException
import com.nullpointer.nullsiteadmin.models.career.CareerContract
import kotlinx.serialization.encodeToString
import org.junit.Assert.*
import org.junit.Test

class EditorialContractTest {
    private val new = BlogMetadata("qa", "Title", "my-post", initialText = "Body")
    private val raw = mapOf<String,Any?>("title" to "Title", "slug" to "my-post", "excerpt" to "", "locale" to "es", "tags" to emptyList<String>(),
        "contentHtml" to "<p>Existing <strong>HTML</strong></p>", "contentFormat" to "html", "contentVersion" to 1L, "status" to "draft", "revision" to 1L)
    @Test fun validMetadataAndTextLimits() {
        assertTrue(EditorialContract.valid(new.copy(title = "x".repeat(180), excerpt = "x".repeat(500), initialText = "x".repeat(10000))))
        listOf(new.copy(title = " "),new.copy(title = "x".repeat(181)),new.copy(excerpt = "x".repeat(501)),new.copy(initialText = ""),new.copy(initialText = "x".repeat(10001))).forEach { assertFalse(EditorialContract.valid(it)) }
    }
    @Test fun slugLocaleIdAndRevisionBoundaries() {
        listOf(new.copy(slug = "Bad Slug"),new.copy(slug = "-bad"),new.copy(slug = "a".repeat(101)),new.copy(locale = "fr"),new.copy(id = "../bad"),new.copy(revision = 9007199254740991),new.copy(revision = -1)).forEach { assertFalse(EditorialContract.valid(it)) }
        assertTrue(EditorialContract.valid(new.copy(locale = "en", slug = "hello-123", id = "a_B-1")))
    }
    @Test fun uniqueTagsMaximumAndPerTagLimit() {
        assertTrue(EditorialContract.valid(new.copy(tagsText = (1..20).joinToString(",") { "tag$it" })))
        listOf("a,a", "x".repeat(41),(1..21).joinToString(",") { "tag$it" }).forEach { assertFalse(EditorialContract.valid(new.copy(tagsText = it))) }
        assertEquals(listOf("one","two"), EditorialContract.tags(new.copy(tagsText = " one, ,two,")))
    }
    @Test fun initialPlainTextCannotInjectMarkupOrImagePaths() {
        val html = EditorialContract.initialHtml("<script>alert('x')</script>\n<img src=\"blog-public/x\"> &")
        assertFalse(html.contains("<script")); assertFalse(html.contains("<img"))
        assertTrue(html.contains("&lt;script&gt;")); assertTrue(html.contains("&quot;")); assertTrue(html.contains("&amp;"))
        assertEquals("<p>a</p><p>b</p>", EditorialContract.initialHtml("a\r\nb"))
    }
    @Test fun patchCannotOverwriteRichTextPublicReferencesOrOtherFields() {
        val value = EditorialContract.readBlog("qa",raw + mapOf("publicationKey" to "es-my-post", "publishedRevision" to 1L))
        val patch = EditorialContract.patch(value.copy(title = "New"))
        assertEquals(setOf("title","slug","excerpt","locale","tags","revision"), patch.keys)
        assertEquals(2L, patch["revision"])
        assertFalse(patch.keys.any { it in listOf("contentHtml","publicationKey","publishedRevision","baseline","status","authorUid") })
    }
    @Test fun fingerprintDetectsBodyRevisionPublicationAndMetadataChanges() {
        val base = EditorialContract.fingerprint(raw)
        listOf(raw + ("contentHtml" to "<p>Remote</p>"),raw + ("revision" to 2L),raw + ("publicationKey" to "es-my-post"),raw + ("title" to "Other"),null).forEach {
            assertThrows(NullAppException.ContentConflict::class.java) { EditorialContract.assertBaseline(it, base) }
        }
        EditorialContract.assertBaseline(raw + ("future" to "preserve"),base)
        assertThrows(NullAppException.ContentConflict::class.java) { EditorialContract.assertBaseline(raw, null) }
        EditorialContract.assertBaseline(null,null)
    }
    @Test fun savedSummaryDoesNotContainBodyOrLoseBaseline() {
        val value = EditorialContract.readBlog("qa",raw + ("contentHtml" to "<p>Secret body</p>".repeat(5000)))
        val encoded = CareerContract.json.encodeToString(value)
        assertFalse(encoded.contains("Secret body")); assertEquals(64,value.baseline!!.length)
        assertEquals(value,CareerContract.json.decodeFromString<BlogMetadata>(encoded))
    }
    @Test fun unsupportedHtmlFormatsAndCommaTagsFailClosed() {
        listOf(raw + ("contentFormat" to "json"),raw + ("contentVersion" to 2L),raw + ("contentVersion" to 1.5),raw + ("revision" to 1.5),raw + ("tags" to listOf("one,two")),raw + ("contentHtml" to "")).forEach {
            assertFalse(EditorialContract.readBlog("qa",it).compatible)
        }
    }
    @Test fun browserHandoffUsesOnlyConfiguredHttpsOriginAndFixedPaths() {
        assertEquals("https://example.com/admin/blog",EditorialContract.editorUrl("https://example.com/",WebEditor.BLOG))
        assertEquals("https://example.com/admin/legal",EditorialContract.editorUrl("https://example.com",WebEditor.LEGAL))
        listOf("http://example.com", "javascript:alert(1)", "https://user:pass@example.com", "https://example.com?token=x", "https://example.com/#x", "https://example.com/other", "https://example.com:8080", "https://example.com.evil@evil.com").forEach { assertNull(EditorialContract.editorUrl(it,WebEditor.BLOG)) }
    }
}
