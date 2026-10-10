package com.nullpointer.nullsiteadmin.ui

import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.test.platform.app.InstrumentationRegistry
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.ui.screens.email.emial.components.lists.ListEmptyEmail
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.formInfoProfile.FormInfoProfileLandscape
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.formInfoProfile.FormInfoProfilePortrait
import com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.subScreens.InfoProfileError
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components.ListInfoProject
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable
import com.nullpointer.nullsiteadmin.ui.share.PasswordTextSavable
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.util.Locale

/** 320 dp content, 200% text, English/Spanish, both palettes; no backend activity. */
@RunWith(Parameterized::class)
class PhaseFourUiTest(private val dark: Boolean, private val language: String) {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    companion object {
        @JvmStatic @Parameterized.Parameters(name = "dark={0}-locale={1}")
        fun cases() = listOf(false, true).flatMap { dark ->
            listOf("en", "es").map { arrayOf(dark, it) }
        }
    }

    @Test fun errorTextAndCounterWrapWithoutClipping() {
        val field = field("error").apply { changeValue("x".repeat(51)) }
        content {
            Column(Modifier.fillMaxHeight().verticalScroll(rememberScrollState())) {
                EditableTextSavable(valueProperty = field)
            }
        }
        val error = rule.onNodeWithText(text(R.string.error_name_admin_length), useUnmergedTree = true)
        error.performScrollTo().assertIsDisplayed()
        assertTextFits(error)
        rule.onNodeWithText("51/50", useUnmergedTree = true).assertIsDisplayed()
        capture("field-length-error")
        rule.runOnIdle { field.changeValue("") }
        rule.onNodeWithText(text(R.string.error_name_admin_empty), useUnmergedTree = true)
            .performScrollTo().assertIsDisplayed()
    }

    @Test fun portraitSaveIsDisabledWhileProfileLoads() = profileLoading(false)
    @Test fun landscapeSaveIsDisabledWhileProfileLoads() = profileLoading(true)

    private fun profileLoading(landscape: Boolean) {
        val name = field("name")
        val profession = field("profession")
        val description = field("description")
        var saved = false
        content {
            val modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState())
            if (landscape) FormInfoProfileLandscape(
                isEnable = false, isDataValid = true, nameAdmin = name,
                professionAdmin = profession, descriptionAdmin = description,
                modifier = modifier, actionSave = { saved = true }, hiddenKeyBoard = {},
            ) else FormInfoProfilePortrait(
                isEnable = false, isDataValid = true, nameAdmin = name,
                professionAdmin = profession, descriptionAdmin = description,
                modifier = modifier, actionSave = { saved = true }, hiddenKeyBoard = {},
            )
        }
        rule.onNodeWithText(text(R.string.text_save_personal_info))
            .performScrollTo().assertIsDisplayed().assertIsNotEnabled()
        val inputs = rule.onAllNodesWithText("Sample")
        inputs.assertCountEquals(3)
        repeat(3) { inputs[it].assertIsNotEnabled() }
        rule.runOnIdle { assertFalse(saved) }
        capture("profile-loading-$landscape")
    }

    @Test fun disabledPasswordCannotBeRevealed() {
        content { PasswordTextSavable(valueProperty = remember { field("password") }, isEnabled = false) }
        rule.onNodeWithContentDescription(text(R.string.description_show_password)).assertIsNotEnabled()
        capture("password-disabled")
    }

    @Test fun projectNextMovesThroughEveryField() {
        val fields = List(4) { field("project-$it") }
        content {
            ListInfoProject(
                urlImgProject = fields[0], nameProject = fields[1],
                urlRepositoryProject = fields[2], descriptionProject = fields[3],
                modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState()),
                hiddenKeyBoard = {}, isEnable = true,
            )
        }
        val inputs = rule.onAllNodes(hasSetTextAction())
        inputs[0].performScrollTo().performClick()
        repeat(3) { index ->
            inputs[index].performImeAction()
            inputs[index + 1].assertIsFocused()
        }
        inputs[3].performScrollTo().assertIsDisplayed()
        capture("project-ime-next")
    }

    @Test fun emptyAndErrorMessagesAreScrollableAndUnclipped() {
        val empty = mutableStateOf(false)
        content { if (empty.value) ListEmptyEmail() else InfoProfileError() }
        val error = rule.onNodeWithText(text(R.string.message_error_load_info_user))
        error.performScrollTo().assertIsDisplayed()
        assertTextFits(error)
        capture("error-state")
        rule.runOnIdle { empty.value = true }
        val message = rule.onNodeWithText(text(R.string.message_empty_contact))
        message.performScrollTo().assertIsDisplayed()
        assertTextFits(message)
        capture("empty-state")
    }

    private fun content(body: @Composable () -> Unit) {
        rule.setContent {
            val base = LocalContext.current
            val localized = remember(base) {
                base.createConfigurationContext(Configuration(base.resources.configuration).apply {
                    setLocale(Locale(language))
                })
            }
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalContext provides localized,
                LocalConfiguration provides localized.resources.configuration,
                LocalDensity provides Density(density.density, 2f),
            ) {
                NullSiteAdminTheme(darkTheme = dark) {
                    Surface(Modifier.fillMaxSize()) {
                        Box(Modifier.fillMaxSize()) {
                            Box(Modifier.width(320.dp).fillMaxHeight().testTag("phase4-content")) { body() }
                        }
                    }
                }
            }
        }
        rule.onNodeWithTag("phase4-content").assertWidthIsEqualTo(320.dp)
    }

    private fun text(id: Int) = rule.activity.createConfigurationContext(
        Configuration(rule.activity.resources.configuration).apply { setLocale(Locale(language)) }
    ).getString(id)

    private fun field(tag: String) = PropertySavableString(
        tag, R.string.hint_name_admin, R.string.label_name_admin, 50, SavedStateHandle(), "Sample",
        R.string.error_name_admin_empty, R.string.error_name_admin_length,
    )

    private fun assertTextFits(node: SemanticsNodeInteraction) {
        val layouts = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertTrue("Text layout must be available", layouts.isNotEmpty())
        layouts.forEach { assertFalse("Text must wrap rather than clip", it.hasVisualOverflow) }
    }

    private fun capture(name: String) {
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        val dir = File(target.filesDir, "phase4-api${Build.VERSION.SDK_INT}-qa").apply { mkdirs() }
        val orientation = rule.activity.resources.configuration.orientation
        val bitmap = rule.onRoot().captureToImage().asAndroidBitmap()
        File(dir, "$name-$language-dark-$dark-orientation-$orientation.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
    }
}
