package com.nullpointer.nullsiteadmin.core.utils

import com.nullpointer.nullsiteadmin.exception.NullAppException
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ContentEditGuardTest {
    private val profile = PersonalInfoData("Name", "https://img", 0, "Developer", "Bio")
    private val project = ProjectData("id", "Project", "Description", "https://img", "https://repo")

    @Test fun unchanged_profile_can_be_saved() {
        val baseline = ContentEditGuard.profileBaseline(profile)!!
        ContentEditGuard.requireUnchanged(baseline, baseline)
    }

    @Test fun each_changed_profile_field_is_a_conflict() {
        val baseline = ContentEditGuard.profileBaseline(profile)!!
        baseline.keys.forEach { key ->
            assertThrows(NullAppException.ContentConflict::class.java) {
                val current = if (key.startsWith("translations.")) {
                    val parts = key.split('.')
                    baseline + ("translations" to mapOf(parts[1] to mapOf(parts[2] to "changed")))
                } else baseline + (key to "changed")
                ContentEditGuard.requireUnchanged(current, baseline)
            }
        }
    }

    @Test fun each_changed_project_field_is_a_conflict() {
        val baseline = ContentEditGuard.projectBaseline(project)
        baseline.forEach { (key, value) ->
            assertThrows(NullAppException.ContentConflict::class.java) {
                val changed = if (value is Boolean) !value else "changed"
                val current = if (key.startsWith("translations.")) {
                    val parts = key.split('.')
                    baseline + ("translations" to mapOf(parts[1] to mapOf(parts[2] to changed)))
                } else baseline + (key to changed)
                ContentEditGuard.requireUnchanged(current, baseline)
            }
        }
    }

    @Test fun unknown_web_fields_and_timestamps_do_not_cause_conflicts() {
        val baseline = ContentEditGuard.projectBaseline(project)
        val remote = baseline + mapOf(
            "translations" to mapOf("fr" to mapOf("name" to "French")),
            "futureField" to listOf(mapOf("url" to "https://capture")),
            "updatedAt" to 123L,
        )
        ContentEditGuard.requireUnchanged(remote, baseline)
        assertEquals("French", ((remote["translations"] as Map<*, *>)["fr"] as Map<*, *>)["name"])
    }

    @Test fun deleting_a_document_while_editing_is_a_conflict() {
        assertThrows(NullAppException.ContentConflict::class.java) {
            ContentEditGuard.requireUnchanged(null, ContentEditGuard.projectBaseline(project))
        }
    }

    @Test fun creating_a_profile_only_allows_an_absent_document() {
        ContentEditGuard.requireUnchanged(null, ContentEditGuard.profileBaseline(null))
        assertThrows(NullAppException.ContentConflict::class.java) {
            ContentEditGuard.requireUnchanged(emptyMap(), null)
        }
    }

    @Test fun legacy_missing_optional_fields_match_their_model_defaults() {
        val baseline = ContentEditGuard.projectBaseline(ProjectData(idProject = "old", name = "Old"))
        ContentEditGuard.requireUnchanged(mapOf("name" to "Old"), baseline)
    }

    @Test fun malformed_field_type_is_not_treated_as_the_expected_value() {
        val baseline = ContentEditGuard.projectBaseline(project)
        assertThrows(NullAppException.ContentConflict::class.java) {
            ContentEditGuard.requireUnchanged(baseline + ("published" to "false"), baseline)
        }
    }
}
