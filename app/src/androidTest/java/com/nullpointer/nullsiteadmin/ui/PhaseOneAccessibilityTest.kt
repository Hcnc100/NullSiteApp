package com.nullpointer.nullsiteadmin.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations
import com.nullpointer.nullsiteadmin.ui.screens.home.componets.ItemNavDrawer
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PhaseOneAccessibilityTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun drawerLightTheme() = checkDrawer(false)
    @Test fun drawerDarkTheme() = checkDrawer(true)

    private fun checkDrawer(darkTheme: Boolean) {
        var clicked = false
        val destination = MainDestinations.ProjectsScreen
        composeRule.setContent {
            NullSiteAdminTheme(darkTheme = darkTheme) {
                val foreground = MaterialTheme.colorScheme.onPrimary.luminance()
                val background = MaterialTheme.colorScheme.primary.luminance()
                val contrast = (maxOf(foreground, background) + 0.05f) /
                    (minOf(foreground, background) + 0.05f)
                assertTrue("Primary text contrast must be at least 4.5:1", contrast >= 4.5f)
                ItemNavDrawer(destination, isSelected = true, onDestinationClicked = { clicked = true })
            }
        }
        composeRule.onNodeWithText(destination.label)
            .assertIsSelected()
            .assertHeightIsAtLeast(48.dp)
            .performClick()
        composeRule.runOnIdle { assertTrue(clicked) }
    }
}
