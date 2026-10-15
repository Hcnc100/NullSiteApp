package com.nullpointer.nullsiteadmin.ui

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.SavedStateHandle
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.domain.career.CareerRepository
import com.nullpointer.nullsiteadmin.domain.editor.EditorialRepository
import com.nullpointer.nullsiteadmin.ui.screens.career.CareerContent
import com.nullpointer.nullsiteadmin.ui.screens.career.CareerViewModel
import com.nullpointer.nullsiteadmin.ui.screens.editor.EditorialContent
import com.nullpointer.nullsiteadmin.ui.screens.editor.EditorialViewModel
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import com.nullpointer.nullsiteadmin.ui.share.AppPullToRefreshBox
import com.nullpointer.nullsiteadmin.ui.share.rememberAppRefreshState
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Locale

/** Real gestures/semantics and real ViewModels; repositories are local, with gated reads. */
@RunWith(Parameterized::class)
class RefreshConsistencyUiTest(private val dark: Boolean, private val language: String) {
    companion object {
        @JvmStatic @Parameterized.Parameters(name = "dark={0}-locale={1}")
        fun cases() = listOf(false, true).flatMap { dark -> listOf("es", "en").map { arrayOf(dark, it) } }
    }
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()
    private fun pull(tag: String) = rule.onNodeWithTag(tag).performTouchInput {
        swipeDown(startY = height * .15f, endY = height * .85f, durationMillis = 700)
    }
    private fun accessibleRefresh(tag: String, label: String) {
        val actions = rule.onNodeWithTag(tag).fetchSemanticsNode().config[SemanticsActions.CustomActions]
        rule.runOnIdle {
            assertEquals(label, actions.single().label)
            assertTrue(actions.single().action())
        }
    }
    private fun assertNoRefreshAction(tag: String) {
        val actions = rule.onNodeWithTag(tag).fetchSemanticsNode().config
            .getOrNull(SemanticsActions.CustomActions)
        assertTrue(actions.isNullOrEmpty())
    }

    @Test fun disablingSharedRefreshKeepsContentOwnerAndLatestCallback() {
        val enabled = mutableStateOf(true)
        val refreshing = mutableStateOf(false)
        val callbackVersion = mutableStateOf(1)
        var firstCalls = 0; var latestCalls = 0; var mounts = 0; var disposals = 0
        val context = rule.activity.createConfigurationContext(Configuration(rule.activity.resources.configuration).apply { setLocale(Locale(language)) })
        rule.setContent { CompositionLocalProvider(LocalContext provides context) {
            NullSiteAdminTheme(darkTheme = dark) {
                val current = callbackVersion.value
                val refresh = rememberAppRefreshState(refreshing.value) { if (current == 1) firstCalls++ else latestCalls++ }
                AppPullToRefreshBox(refresh, Modifier.fillMaxSize().testTag("shared-refresh"), enabled.value) {
                    DisposableEffect(Unit) { mounts++; onDispose { disposals++ } }
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) { Text("Stable content") }
                }
            }
        } }
        accessibleRefresh("shared-refresh", context.getString(R.string.refresh_content))
        rule.runOnIdle { assertEquals(1, firstCalls); enabled.value = false; callbackVersion.value = 2 }
        assertNoRefreshAction("shared-refresh")
        pull("shared-refresh")
        rule.runOnIdle { assertEquals(0, latestCalls); assertEquals(1, mounts); assertEquals(0, disposals); enabled.value = true }
        accessibleRefresh("shared-refresh", context.getString(R.string.refresh_content))
        rule.runOnIdle { assertEquals(1, latestCalls); refreshing.value = true }
        assertNoRefreshAction("shared-refresh")
        pull("shared-refresh")
        rule.runOnIdle { assertEquals(1, latestCalls); assertEquals(1, mounts); assertEquals(0, disposals); refreshing.value = false }
    }

    @Test fun careerPullRetriesAndProtectsSkillDraftAndSelectedPdf() {
        val source = CareerQaRepository()
        var reads = 0
        var fail = true
        var gate: CompletableDeferred<Unit>? = null
        val repository = object : CareerRepository by source {
            override suspend fun load(): com.nullpointer.nullsiteadmin.models.career.CareerSnapshot {
                reads++; gate?.await()
                if (fail) error("QA offline")
                return source.load()
            }
        }
        lateinit var vm: CareerViewModel
        rule.runOnUiThread { vm = CareerViewModel(repository, SavedStateHandle()) }
        val context = rule.activity.createConfigurationContext(Configuration(rule.activity.resources.configuration).apply { setLocale(Locale(language)) })
        rule.setContent { CompositionLocalProvider(LocalContext provides context) {
            NullSiteAdminTheme(darkTheme = dark) { CareerContent(vm) {} }
        } }
        rule.waitUntil(5000) { vm.busy == null && vm.error != null }
        rule.onNodeWithTag("career-retry").assertExists()
        gate = CompletableDeferred(); fail = false
        pull("career-refresh-container")
        rule.waitUntil(5000) { reads == 2 && vm.busy == "load" }
        assertNoRefreshAction("career-refresh-container")
        pull("career-refresh-container")
        rule.runOnIdle { assertEquals(2, reads) }
        gate!!.complete(Unit)
        rule.waitUntil(5000) { vm.busy == null && vm.snapshot != null }
        gate = null
        rule.onNodeWithTag("career-retry").assertDoesNotExist()
        accessibleRefresh("career-refresh-container", context.getString(R.string.refresh_content))
        rule.waitUntil(5000) { reads == 3 && vm.busy == null }
        rule.runOnUiThread { vm.edit(vm.snapshot!!.skills.single()); vm.update(vm.draft!!.copy(name = "Keep my draft")) }
        assertNoRefreshAction("career-refresh-container")
        pull("career-refresh-container")
        rule.runOnIdle { assertEquals(3, reads); assertEquals("Keep my draft", vm.draft!!.name) }
        rule.runOnUiThread { vm.discard() }
        rule.waitUntil(5000) { vm.busy == null && vm.draft == null }
        rule.runOnUiThread { vm.changeTab("resume"); vm.selectPdf(Uri.parse("content://qa/pending.pdf")) }
        val before = reads
        assertNoRefreshAction("career-refresh-container")
        pull("career-refresh-container")
        rule.runOnIdle { assertEquals(before, reads); assertEquals("content://qa/pending.pdf", vm.pendingUri) }
    }

    @Test fun editorialPullRetriesAndProtectsMetadataDraft() {
        val source = EditorialQaRepository()
        var reads = 0
        var fail = true
        var gate: CompletableDeferred<Unit>? = null
        val repository = object : EditorialRepository by source {
            override suspend fun load(): com.nullpointer.nullsiteadmin.models.editor.EditorialSnapshot {
                reads++; gate?.await()
                if (fail) error("QA offline")
                return source.load()
            }
        }
        lateinit var vm: EditorialViewModel
        rule.runOnUiThread { vm = EditorialViewModel(repository, SavedStateHandle()) }
        val context = rule.activity.createConfigurationContext(Configuration(rule.activity.resources.configuration).apply { setLocale(Locale(language)) })
        rule.setContent { CompositionLocalProvider(LocalContext provides context) {
            NullSiteAdminTheme(darkTheme = dark) { EditorialContent(vm) {} }
        } }
        rule.waitUntil(5000) { !vm.busy && vm.error != null }
        rule.onNodeWithTag("editor-retry").assertExists()
        gate = CompletableDeferred(); fail = false
        pull("editor-refresh-container")
        rule.waitUntil(5000) { reads == 2 && vm.refreshing }
        assertNoRefreshAction("editor-refresh-container")
        pull("editor-refresh-container")
        rule.runOnIdle { assertEquals(2, reads) }
        gate!!.complete(Unit)
        rule.waitUntil(5000) { !vm.busy && vm.snapshot != null }
        gate = null
        rule.onNodeWithTag("editor-retry").assertDoesNotExist()
        accessibleRefresh("editor-refresh-container", context.getString(R.string.refresh_content))
        rule.waitUntil(5000) { reads == 3 && !vm.busy }
        rule.runOnUiThread { vm.section("legal") }
        accessibleRefresh("editor-refresh-container", context.getString(R.string.refresh_content))
        rule.waitUntil(5000) { reads == 4 && !vm.busy }
        rule.runOnUiThread { vm.section("blog"); vm.edit(vm.snapshot!!.blogs.single()); vm.update(vm.draft!!.copy(title = "Keep metadata")) }
        assertNoRefreshAction("editor-refresh-container")
        pull("editor-refresh-container")
        rule.runOnIdle { assertEquals(4, reads); assertEquals("Keep metadata", vm.draft!!.title) }
    }
}
