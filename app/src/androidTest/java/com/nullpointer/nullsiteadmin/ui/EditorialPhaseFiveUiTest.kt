package com.nullpointer.nullsiteadmin.ui

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.lifecycle.SavedStateHandle
import androidx.test.espresso.Espresso
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.editor.WebEditor
import com.nullpointer.nullsiteadmin.ui.screens.editor.EditorialContent
import com.nullpointer.nullsiteadmin.ui.screens.editor.EditorialViewModel
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.util.Locale

@RunWith(Parameterized::class)
class EditorialPhaseFiveUiTest(private val dark: Boolean, private val language: String) {
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

    @Test fun metadataValidationHandoffAndLegalHistoryAtLargeFont() {
        val repo = EditorialQaRepository(); lateinit var vm: EditorialViewModel
        var opened: WebEditor? = null
        original = Configuration(rule.activity.resources.configuration)
        val config = Configuration(original).apply { setLocale(Locale(language)); fontScale = 2f }
        @Suppress("DEPRECATION")
        rule.runOnUiThread { rule.activity.resources.updateConfiguration(config, rule.activity.resources.displayMetrics); vm = EditorialViewModel(repo, SavedStateHandle()) }
        val strings = rule.activity.createConfigurationContext(config)
        rule.setContent {
            CompositionLocalProvider(LocalContext provides strings, LocalConfiguration provides config,
                LocalDensity provides Density(LocalDensity.current.density, 2f)) {
                NullSiteAdminTheme(darkTheme = dark) { EditorialContent(vm) { opened = it } }
            }
        }
        rule.waitUntil(5000) { vm.snapshot != null && !vm.busy }
        rule.onNodeWithTag("editor-edit-qa").performScrollTo().performClick()
        rule.onNodeWithTag("editor-name").performScrollTo().performTextReplacement("Editorial QA")
        rule.onNodeWithTag("editor-slug").performScrollTo().performTextReplacement("INVALID slug")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithTag("editor-save").performScrollTo().assertIsNotEnabled()
        rule.onNodeWithTag("editor-slug").performScrollTo().performTextReplacement("editorial-qa")
        rule.onNodeWithTag("editor-locale-en").performScrollTo().performClick()
        rule.onNodeWithTag("editor-tags").performScrollTo().performTextReplacement("Android, Web")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithTag("editor-open-web").performScrollTo().performClick()
        rule.onNodeWithText(strings.getString(R.string.editor_session_help)).assertExists()
        rule.onNodeWithTag("editor-confirm").performClick()
        rule.runOnIdle { assertEquals(WebEditor.BLOG,opened); assertEquals("Editorial QA",vm.draft!!.title); assertEquals(0,repo.saves) }
        rule.onNodeWithTag("editor-save").performScrollTo().assertIsEnabled().performClick()
        rule.waitUntil(5000) { vm.draft == null && !vm.busy }
        assertEquals("en",repo.lastSaved!!.locale)
        assertEquals(listOf("Android","Web"),repo.raw["tags"])
        assertEquals("es-article-qa",repo.raw["publicationKey"])
        rule.onNodeWithTag("editor-tab-legal").performScrollTo().performClick()
        rule.onNodeWithTag("editor-history-privacy").performScrollTo().performClick()
        rule.waitUntil(5000) { vm.history != null && !vm.busy }
        assertEquals(1L,vm.history!!.single().revision)
        rule.onNodeWithTag("editor-open-web").performScrollTo().performClick()
        rule.onNodeWithTag("editor-confirm").performClick()
        rule.runOnIdle { assertEquals(WebEditor.LEGAL,opened); assertEquals(1,repo.saves) }
        val dir = File(rule.activity.getExternalFilesDir(null),"phase5-editorial-qa").apply { mkdirs() }
        rule.waitForIdle()
        File(dir,"legal-$language-dark-$dark-font-2-orientation-${config.orientation}.png").outputStream().use {
            rule.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)
        }
    }
}
