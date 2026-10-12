package com.nullpointer.nullsiteadmin.models.career

import com.google.firebase.Timestamp
import com.nullpointer.nullsiteadmin.exception.NullAppException
import kotlinx.serialization.encodeToString
import org.junit.Assert.*
import org.junit.Test

class CareerContractTest {
    private val skill = SkillContent(id = "kotlin", name = "Kotlin", published = true)
    @Test fun catalogMatchesWebAndDefaultsValidate() {
        assertEquals(listOf("python", "java", "javascript", "android", "angular", "github", "git", "docker"), CareerContract.icons)
        assertEquals(9, CareerContract.defaultSkills.size)
        assertTrue(CareerContract.defaultSkills.all(CareerContract::valid))
        assertEquals("android", CareerContract.defaultSkills.first { it.id == "kotlin" }.iconKey)
    }
    @Test fun textAndOrderBoundaries() {
        assertTrue(CareerContract.valid(skill.copy(name = "n".repeat(120), description = "d".repeat(10000), sortOrder = "9999")))
        listOf(skill.copy(name = " "), skill.copy(name = "n".repeat(121)), skill.copy(description = "d".repeat(10001)),
            skill.copy(sortOrder = "-1"), skill.copy(sortOrder = "10000"), skill.copy(sortOrder = "1.5"), skill.copy(sortOrder = "abc")).forEach { assertFalse(CareerContract.valid(it)) }
    }
    @Test fun rejectsArbitraryIconsCategoriesAndColors() {
        listOf(skill.copy(iconKey = "<svg>"), skill.copy(category = "other"), skill.copy(color = "red"), skill.copy(color = "#123"), skill.copy(color = "#00000g")).forEach { assertFalse(CareerContract.valid(it)) }
        assertTrue(CareerContract.valid(skill.copy(category = "tech", color = "#aBcD09")))
    }
    @Test fun validatesBothTranslatedLocales() {
        CareerContract.locales.forEach { locale ->
            assertFalse(CareerContract.valid(skill.copy(translations = mapOf(locale to mapOf("name" to "x".repeat(121))))))
            assertFalse(CareerContract.valid(skill.copy(translations = mapOf(locale to mapOf("description" to "x".repeat(10001))))))
        }
    }
    @Test fun createIsNestedAndUpdatePreservesUnknownLocalesAndFields() {
        val value = skill.copy(name = " Kotlin ", baseline = "never send", translations = mapOf("en" to mapOf("name" to " Kotlin EN "), "fr" to mapOf("name" to "Kotlin FR")))
        val patch = CareerContract.skillPatch(value)
        assertEquals("Kotlin", patch["name"]); assertEquals("Kotlin EN", patch["translations.en.name"])
        assertFalse(patch.keys.any { it in listOf("id", "baseline", "translations", "createdAt") })
        val create = CareerContract.skillCreate(value)
        assertFalse(create.keys.any { it.contains('.') || it == "baseline" })
        assertTrue((create["translations"] as Map<*, *>).containsKey("fr"))
        assertEquals(0L, create["sortOrder"])
    }
    @Test fun canonicalIgnoresMapOrderButNotArrayOrderOrTimestamp() {
        assertEquals(CareerContract.canonical(mapOf("b" to 2, "a" to 1)), CareerContract.canonical(mapOf("a" to 1, "b" to 2)))
        assertNotEquals(CareerContract.canonical(listOf(1,2)), CareerContract.canonical(listOf(2,1)))
        assertNotEquals(CareerContract.canonical(Timestamp(1,0)), CareerContract.canonical(Timestamp(1,1)))
    }
    @Test fun sparseReadAndSavedDraftRetainOriginalFingerprint() {
        val raw = mapOf<String, Any?>("name" to "Old", "published" to true)
        val read = CareerContract.readSkill("old", raw)
        val restore = CareerContract.json.decodeFromString<SkillContent>(CareerContract.json.encodeToString(read.copy(name = "Draft")))
        assertEquals(read.baseline, restore.baseline); CareerContract.assertSkillBaseline(raw, restore.baseline)
    }
    @Test fun changedKnownFieldsDeletedDocsAndCreateCollisionsConflict() {
        val raw = CareerContract.skillCreate(skill); val base = CareerContract.skillFingerprint(raw)
        CareerContract.assertSkillBaseline(raw + ("future" to "preserved"), base)
        listOf(raw + ("name" to "Remote"), raw + ("published" to false), raw + ("translations" to mapOf("fr" to mapOf("name" to "FR"))), null).forEach { current ->
            assertThrows(NullAppException.ContentConflict::class.java) { CareerContract.assertSkillBaseline(current, base) }
        }
        assertThrows(NullAppException.ContentConflict::class.java) { CareerContract.assertSkillBaseline(raw, null) }
        CareerContract.assertSkillBaseline(null, null)
    }
    @Test fun resumeConflictIsPerLocaleAndIncludesEnabledTimestampAndUnknownMetadata() {
        val current = mapOf<String,Any?>("enabled" to true, "storagePath" to "resumes/es/a.pdf", "updatedAt" to Timestamp(1,0))
        val base = CareerContract.canonical(current)
        CareerContract.assertResumeBaseline(current, base)
        listOf(current + ("enabled" to false), current + ("updatedAt" to Timestamp(2,0)), current + ("future" to 1), null).forEach {
            assertThrows(NullAppException.ContentConflict::class.java) { CareerContract.assertResumeBaseline(it, base) }
        }
        CareerContract.assertResumeBaseline(null, "null")
    }
    @Test fun pdfChecksMimeExtensionHeaderAndMinimum() {
        val valid = "%PDF-1.7 test".toByteArray()
        assertTrue(CareerContract.validPdf("CV.PDF", "application/pdf", valid))
        assertFalse(CareerContract.validPdf("CV.png", "application/pdf", valid))
        assertFalse(CareerContract.validPdf("CV.pdf", "image/png", valid))
        assertFalse(CareerContract.validPdf("CV.pdf", "application/pdf", "not-a-pdf".toByteArray()))
        assertFalse(CareerContract.validPdf("CV.pdf", "application/pdf", "%PDF-".toByteArray()))
    }
    @Test fun pdfMaximumBoundary() {
        val max = ByteArray(CareerContract.MAX_PDF_BYTES); "%PDF-".toByteArray().copyInto(max)
        assertTrue(CareerContract.validPdf("cv.pdf", "application/pdf", max))
        assertFalse(CareerContract.validPdf("cv.pdf", "application/pdf", max.copyOf(max.size+1)))
    }
    @Test fun resumePathsNeverCrossLocaleOrAcceptDownloadUrls() {
        val entry = ResumeContent("resumes/es/a-1.pdf", "CV-es.pdf", 20, true)
        assertTrue(CareerContract.activeResume("es", entry)); assertFalse(CareerContract.activeResume("en", entry))
        assertTrue(CareerContract.activeResume("en", entry.copy(storagePath = "resumes/en/a-1.pdf")))
        assertFalse(CareerContract.activeResume("es", entry.copy(enabled = false)))
        assertFalse(CareerContract.activeResume("es", entry.copy(sizeBytes = 0)))
        assertFalse(CareerContract.validResumePath("es", "resumes/es/../en/a.pdf"))
        assertFalse(CareerContract.validResumePath("es", "https://example.com/cv.pdf"))
        assertFalse(CareerContract.validResumePath("fr", "resumes/fr/a.pdf"))
    }
}
