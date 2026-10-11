package com.nullpointer.nullsiteadmin.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.semantics.Role
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.ui.screens.project.projectScreen.componets.projectItem.ProjectItem
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProjectCardAlignmentTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    @Test fun shortDescriptionKeepsImageAndEditButtonCenteredInCard() {
        rule.setContent {
            NullSiteAdminTheme {
                ProjectItem(
                    projectData = ProjectData(name = "Archipelago", description = "My first technical test"),
                    modifier = Modifier.fillMaxWidth().testTag("project-card"),
                    actionEditProject = {},
                )
            }
        }
        val cardCenter = rule.onNodeWithTag("project-card").fetchSemanticsNode().boundsInRoot.center.x
        val imageCenter = rule.onNodeWithContentDescription(
            rule.activity.getString(R.string.description_current_img_project)
        ).fetchSemanticsNode().boundsInRoot.center.x
        val buttonCenter = rule.onNode(SemanticsMatcher.expectValue(
            androidx.compose.ui.semantics.SemanticsProperties.Role, Role.Button
        )).fetchSemanticsNode().boundsInRoot.center.x
        assertEquals(cardCenter, imageCenter, 1f)
        assertEquals(cardCenter, buttonCenter, 1f)
    }
}
