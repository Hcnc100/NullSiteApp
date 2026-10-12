package com.nullpointer.nullsiteadmin.ui

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.lifecycle.SavedStateHandle
import androidx.test.espresso.Espresso
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.screens.career.CareerContent
import com.nullpointer.nullsiteadmin.ui.screens.career.CareerViewModel
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.util.Locale

@RunWith(Parameterized::class)
class CareerPhaseFourUiTest(private val dark: Boolean, private val language: String) {
    companion object {
        @JvmStatic @Parameterized.Parameters(name = "dark={0}-locale={1}")
        fun cases() = listOf(false,true).flatMap { dark -> listOf("es","en").map { arrayOf(dark,it) } }
    }
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()
    private var original: Configuration? = null
    @After fun restore() { original?.let { config ->
        @Suppress("DEPRECATION")
        rule.runOnUiThread { rule.activity.resources.updateConfiguration(config, rule.activity.resources.displayMetrics) }
    } }

    @Test fun bilingualCrudPdfReplacementAndWithdrawalAtLargeFont() {
        val repo = CareerQaRepository(); lateinit var vm: CareerViewModel
        original = Configuration(rule.activity.resources.configuration)
        val config = Configuration(original).apply { setLocale(Locale(language)); fontScale = 2f }
        @Suppress("DEPRECATION")
        rule.runOnUiThread { rule.activity.resources.updateConfiguration(config, rule.activity.resources.displayMetrics); vm = CareerViewModel(repo, SavedStateHandle()) }
        val strings = rule.activity.createConfigurationContext(config)
        rule.setContent {
            CompositionLocalProvider(LocalContext provides strings, LocalConfiguration provides config,
                LocalDensity provides Density(LocalDensity.current.density, 2f)) {
                NullSiteAdminTheme(darkTheme = dark) { CareerContent(vm) { vm.selectPdf(Uri.parse("content://qa/cv.pdf")) } }
            }
        }
        rule.waitUntil(5000) { vm.snapshot != null && vm.busy == null }
        rule.onNodeWithTag("career-edit-qa").performScrollTo().performClick()
        rule.onNodeWithTag("skill-name").performScrollTo().performTextReplacement("Kotlin QA")
        rule.onNodeWithTag("skill-order").performScrollTo().performTextReplacement("10000")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithTag("career-save").performScrollTo().assertIsNotEnabled()
        rule.onNodeWithTag("career-skill-invalid").assertExists()
        rule.onNodeWithTag("skill-order").performScrollTo().performTextReplacement("9")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithTag("career-locale-en").performScrollTo().performClick()
        rule.onNodeWithTag("skill-translated-name").performScrollTo().performTextReplacement("Kotlin English")
        rule.onNodeWithTag("career-locale-es").performScrollTo().performClick()
        rule.onNodeWithTag("skill-translated-name").performScrollTo().performTextReplacement("Kotlin Español")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithTag("career-save").performScrollTo().assertIsEnabled()
        capture("skill", config.orientation)
        rule.onNodeWithTag("career-save").performClick()
        rule.waitUntil(5000) { vm.draft == null && vm.busy == null }
        assertEquals("Kotlin English", repo.lastSaved!!.translations["en"]!!["name"])
        assertEquals("Kotlin Español", repo.lastSaved!!.translations["es"]!!["name"])
        assertEquals("9", repo.lastSaved!!.sortOrder)

        rule.onNodeWithTag("career-edit-qa").performScrollTo().performClick()
        rule.onNodeWithTag("career-delete").performScrollTo().performClick()
        rule.onNodeWithText(strings.getString(R.string.profile_keep_editing)).performClick()
        assertEquals(0, repo.deleteCalls)
        rule.onNodeWithTag("career-delete").performClick()
        rule.onNodeWithTag("career-confirm").performClick()
        rule.waitUntil(5000) { vm.draft == null && vm.busy == null }; assertEquals(1, repo.deleteCalls)

        rule.onNodeWithTag("career-tab-resume").performScrollTo().performClick()
        rule.onNodeWithTag("career-select-pdf").performScrollTo().performClick()
        rule.onNodeWithTag("career-locale-en").performScrollTo().assertIsNotEnabled()
        rule.onNodeWithTag("career-publish").performScrollTo().performClick()
        rule.waitUntil(5000) { vm.pendingUri == null && vm.busy == null }
        assertEquals(1, repo.publishCalls); assertTrue(repo.resumes["es"]!!.storagePath.contains("new-"))
        rule.onNodeWithTag("career-withdraw").performScrollTo().performClick()
        rule.onNodeWithText(strings.getString(R.string.career_withdraw_help)).assertExists()
        rule.onNodeWithTag("career-dialog-body").performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 2000f) }
        rule.onNodeWithTag("career-confirm").performClick()
        rule.waitUntil(5000) { vm.busy == null && vm.snapshot!!.resumes["es"]!!.enabled == false }
        assertTrue(repo.resumes["en"]!!.enabled)
        rule.onNodeWithTag("career-resume-status").performScrollTo().assertTextEquals(strings.getString(R.string.career_resume_inactive))
        capture("resume", config.orientation)
    }
    private fun capture(name: String, orientation: Int) {
        val dir = File(rule.activity.getExternalFilesDir(null),"phase4-career-qa").apply { mkdirs() }
        File(dir,"$name-$language-dark-$dark-font-2-orientation-$orientation.png").outputStream().use {
            rule.waitForIdle()
            val bitmap = rule.onRoot().captureToImage().asAndroidBitmap()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)
        }
    }
}
