package com.nullpointer.nullsiteadmin.ui

import android.graphics.Bitmap
import android.content.res.Configuration
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import androidx.lifecycle.SavedStateHandle
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableImg
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.ui.screens.login.LoginScreen
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.EditInfoProfileScreen
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.EditProjectScreen
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

/** Real IME + production screen composables, without authentication or remote writes. */
@RunWith(Parameterized::class)
class FormKeyboardTest(private val form: String, private val dark: Boolean, private val scale: Float) {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    companion object {
        @JvmStatic @Parameterized.Parameters(name = "{0}-dark={1}-scale={2}")
        fun cases(): List<Array<Any>> = listOf("login", "profile", "project").flatMap { form ->
            listOf(false, true).flatMap { dark -> listOf(1f, 2f).map { scale -> arrayOf(form, dark, scale) } }
        }
    }

    private fun field(index: Int): PropertySavableString {
        // Same labels/hints as production ViewModels; values remain local fixtures.
        val resources = when (form) {
            "login" -> listOf(R.string.hint_email to R.string.label_email,
                R.string.hint_password to R.string.label_password)
            "project" -> listOf(R.string.hint_img_project to R.string.label_img_project,
                R.string.hint_name_project to R.string.label_name_project,
                R.string.hint_repo_project to R.string.label_repo_project,
                R.string.hint_description_project to R.string.label_description_project)
            else -> listOf(R.string.hint_name_admin to R.string.label_name_admin,
                R.string.hint_profession_admin to R.string.label_profession_admin,
                R.string.hint_description_admin to R.string.label_description_admin)
        }
        val (hint, label) = resources[index.coerceAtMost(resources.lastIndex)]
        return PropertySavableString("field-$index", hint, label, 500, SavedStateHandle(), "Sample")
    }

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    @Test fun lastFieldAndSubmitRemainReachableWithKeyboard() {
        val ime = AtomicInteger()
        var submitted = false
        val fields = List(4) { field(it) }
        val useSystemFontScale = InstrumentationRegistry.getArguments()
            .getString("qaUseSystemFontScale") == "true"
        rule.runOnUiThread {
            WindowCompat.setDecorFitsSystemWindows(rule.activity.window, false)
            rule.activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        rule.setContent {
            val density = LocalDensity.current
            if (useSystemFontScale) assertEquals("System font scale must be 200%", 2f, density.fontScale, 0.01f)
            CompositionLocalProvider(LocalDensity provides if (useSystemFontScale) density else Density(density.density, scale)) {
                val bottom = WindowInsets.ime.getBottom(LocalDensity.current)
                SideEffect { ime.set(bottom) }
                NullSiteAdminTheme(darkTheme = dark) {
                    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        when (form) {
                            "login" -> LoginScreen(
                                snackbarHostState = remember { SnackbarHostState() }, isAuthenticating = false,
                                email = fields[0], password = fields[1], onAuthAction = { submitted = true }
                            )
                            "project" -> EditProjectScreen(
                                snackbarHostState = remember { SnackbarHostState() }, isUpdatedProject = false,
                                isDataValid = true, urlImgProject = fields[0], nameProject = fields[1],
                                urlRepositoryProject = fields[2], descriptionProject = fields[3],
                                onEditProjectAction = { submitted = true }
                            )
                            else -> EditInfoProfileScreen(
                                isLoading = false, isDataValid = true, snackbarHostState = remember { SnackbarHostState() },
                                imageProfile = PropertySavableImg("image", SavedStateHandle()),
                                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                                nameAdmin = fields[0], professionAdmin = fields[1], descriptionAdmin = fields[2],
                                actionBeforeSelect = {}, onEditInfoProfileActions = { submitted = true }
                            )
                        }
                    }
                }
            }
        }
        InstrumentationRegistry.getArguments().getString("qaOrientation")?.let { requested ->
            val expected = when (requested) {
                "portrait" -> Configuration.ORIENTATION_PORTRAIT
                "landscape" -> Configuration.ORIENTATION_LANDSCAPE
                else -> error("Unknown QA orientation: $requested")
            }
            rule.runOnIdle {
                assertEquals("Emulator orientation drifted", expected,
                    rule.activity.resources.configuration.orientation)
            }
        }
        val inputs = rule.onAllNodes(hasSetTextAction())
        val last = inputs[inputs.fetchSemanticsNodes().size - 1]
        last.performScrollTo().performClick()
        rule.waitUntil(10_000) { ime.get() > 0 }
        last.performTextInput(" typed")
        last.performScrollTo().assertIsDisplayed()
        val editorTitle = when (form) {
            "profile" -> R.string.title_edit_info_personal
            "project" -> R.string.title_edit_project
            else -> null
        }?.let(rule.activity::getString)
        if (editorTitle != null && rule.activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rule.onNodeWithText(editorTitle).assertDoesNotExist()
        }
        assertAboveKeyboard(last, ime.get())
        capture("field")

        val label = when (form) {
            "login" -> R.string.text_auth_button
            "project" -> R.string.text_update_project
            else -> R.string.text_save_personal_info
        }
        val button = rule.onNodeWithText(rule.activity.getString(label))
        scrollIfSupported(button)
        button.assertIsDisplayed()
        assertAboveKeyboard(button, ime.get())
        capture("submit")
        // Dismiss without submitting or touching any backend.
        Espresso.pressBack()
        rule.waitUntil(10_000) { ime.get() == 0 }
        editorTitle?.let { rule.onNodeWithText(it).assertIsDisplayed() }
        scrollIfSupported(button)
        button.performClick()
        rule.runOnIdle { assertTrue(submitted) }
    }

    private fun assertAboveKeyboard(node: SemanticsNodeInteraction, imeBottom: Int) {
        // Use unclipped bounds: clipped semantics could hide a partially obscured control.
        val bounds = node.getUnclippedBoundsInRoot()
        val rootTop = rule.onRoot().fetchSemanticsNode().boundsInWindow.top
        val bottomPx = bounds.bottom.value * rule.density.density + rootTop
        val topPx = bounds.top.value * rule.density.density + rootTop
        var bottom = 0
        rule.runOnUiThread { bottom = rule.activity.window.decorView.height - imeBottom }
        assertTrue("$form overlaps IME: $bounds, available bottom=$bottom", bottomPx <= bottom + 1)
        assertTrue("$form clipped above viewport: $bounds", topPx >= 0)
    }

    private fun scrollIfSupported(node: SemanticsNodeInteraction) {
        // Landscape project submit is in a stationary column beside the scrollable form.
        if (hasAnyAncestor(hasScrollAction()).matches(node.fetchSemanticsNode())) {
            node.performScrollTo()
        }
    }

    private fun capture(step: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val orientation = rule.activity.resources.configuration.orientation
        val outputDirectory = InstrumentationRegistry.getArguments()
            .getString("qaOutputDirectory", "phase1-qa")!!
        require(outputDirectory.matches(Regex("[a-z0-9-]+")))
        val dir = File(instrumentation.targetContext.filesDir, outputDirectory).apply { mkdirs() }
        val image = instrumentation.uiAutomation.takeScreenshot()
        File(dir, "$form-dark-$dark-scale-$scale-orientation-$orientation-$step.png").outputStream().use {
            image.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        image.recycle()
    }
}
