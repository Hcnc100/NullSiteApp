package com.nullpointer.nullsiteadmin.ui

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.semantics.SemanticsActions
import androidx.lifecycle.SavedStateHandle
import androidx.test.espresso.Espresso
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.StorageTaskResult
import com.nullpointer.nullsiteadmin.domain.image.ImageRepository
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepository
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.dto.PersonalInfoDTO
import com.nullpointer.nullsiteadmin.models.personalInfo.wrapper.UpdateInfoProfileWrapper
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.EditInfoProfileScreen
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.viewModel.EditInfoViewModel
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import com.ramcosta.composedestinations.spec.Direction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.After
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

/** Actual production editor and ViewModel; repository boundaries are local, never Firebase. */
@RunWith(Parameterized::class)
class ProfilePhaseTwoUiTest(private val dark: Boolean, private val language: String, private val scale: Float) {
    companion object {
        @JvmStatic @Parameterized.Parameters(name = "dark={0}-language={1}-scale={2}")
        fun cases() = listOf(false, true).flatMap { dark ->
            listOf("es", "en").flatMap { language -> listOf(1f, 2f).map { arrayOf(dark, language, it) } }
        }
    }
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var vm: EditInfoViewModel
    private lateinit var strings: android.content.Context
    private var originalConfiguration: Configuration? = null
    private val repo = LocalProfileRepository()
    private val backs = AtomicInteger()
    private val original = PersonalInfoData("NullSite QA", "", 1, "Base developer", "Base biography",
        translations = mapOf("es" to mapOf("profession" to "Desarrollador Android", "description" to "Biografía en español."),
            "en" to mapOf("profession" to "Android developer", "description" to "English biography.")))

    private fun editor() {
        rule.runOnUiThread { vm = EditInfoViewModel(SavedStateHandle(), repo, LocalImages) }
        originalConfiguration = Configuration(rule.activity.resources.configuration)
        val config = Configuration(originalConfiguration).apply {
            setLocale(Locale(language))
            fontScale = scale
        }
        // Dialogs obtain resources from the Activity view context, not only LocalContext.
        @Suppress("DEPRECATION")
        rule.runOnUiThread { rule.activity.resources.updateConfiguration(config, rule.activity.resources.displayMetrics) }
        strings = rule.activity.createConfigurationContext(config)
        rule.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides rule.activity,
                LocalContext provides strings, LocalConfiguration provides config,
                LocalDensity provides Density(density, scale)) {
                NullSiteAdminTheme(darkTheme = dark) {
                    EditInfoProfileScreen(original, object : ActionRootDestinations {
                        override fun backDestination(): Boolean { backs.incrementAndGet(); return true }
                        override fun changeRoot(direction: Direction) = error("Not used")
                        override fun changeRoot(route: Uri) = error("Not used")
                    }, editInfoVM = vm)
                }
            }
        }
        rule.waitForIdle()
    }

    @After fun restoreConfiguration() {
        originalConfiguration?.let { config ->
            @Suppress("DEPRECATION")
            rule.runOnUiThread { rule.activity.resources.updateConfiguration(config, rule.activity.resources.displayMetrics) }
        }
    }

    @Test fun bilingualDraftPreviewPreservesBothLanguagesAndFitsViewport() {
        editor()
        rule.onNodeWithTag("profile-locale-en").performScrollTo().performClick()
        rule.onNodeWithTag("profile-translation-profession").performScrollTo().performTextReplacement("Mobile engineer")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithTag("profile-toggle-preview").performScrollTo().performClick()
        rule.onNodeWithTag("profile-preview-profession").performScrollTo().assertTextEquals("Mobile engineer").assertIsDisplayed()
        capture("preview-en")
        rule.onNodeWithTag("profile-locale-es").performScrollTo().performClick()
        rule.onNodeWithTag("profile-preview-profession").performScrollTo().assertTextEquals("Desarrollador Android").assertIsDisplayed()
        val card = rule.onNodeWithTag("profile-content-preview").fetchSemanticsNode().boundsInRoot
        val root = rule.onRoot().fetchSemanticsNode().boundsInRoot
        assertTrue("Preview must not overflow horizontally", card.left >= root.left && card.right <= root.right)
        capture("preview-es")
    }

    @Test fun invalidHttpsDisablesSaveAndClearingLinksIsExplicit() {
        editor()
        rule.onNodeWithTag("profile-toggle-links").performScrollTo().performClick()
        rule.onNodeWithTag("profile-social-github").performScrollTo().performTextReplacement("http://example.com")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithText(strings.getString(R.string.text_save_personal_info)).performScrollTo().assertIsNotEnabled()
        rule.onNodeWithTag("profile-social-github").performScrollTo().assertIsDisplayed()
        capture("invalid-https")
        rule.onNodeWithTag("profile-social-github").performTextReplacement("https://github.com/nullsite-qa")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithText(strings.getString(R.string.text_save_personal_info)).performScrollTo().assertIsEnabled()
        rule.onNodeWithTag("profile-clear-links").performScrollTo().performClick()
        rule.runOnIdle { assertEquals(emptyList<Any>(), vm.profileContent.content.socialLinks) }
        capture("links-cleared")
    }

    @Test fun savingEnrichedContentOnlyKeepsBaseFieldsOutOfPatch() {
        editor()
        rule.onNodeWithTag("profile-locale-en").performScrollTo().performClick()
        rule.onNodeWithTag("profile-translation-description").performScrollTo().performTextReplacement("Updated English biography")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithTag("profile-toggle-links").performScrollTo().performClick()
        rule.onNodeWithTag("profile-social-github").performScrollTo().performTextReplacement("https://github.com/nullsite-qa")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithText(strings.getString(R.string.text_save_personal_info)).performScrollTo().performClick()
        rule.waitUntil(5_000) { repo.saved != null && backs.get() == 1 }
        val request = repo.saved!!
        assertEquals(original, request.baseline)
        assertNull(request.name)
        assertNull(request.profession)
        assertNull(request.description)
        assertNull(request.uriFileImgProfile)
        val patch = PersonalInfoDTO.fromPersonalInfoWrapper(null, request).toUpdateMap()
        assertEquals("Updated English biography", patch["translations.en.description"])
        assertTrue(patch.containsKey("socialLinks"))
        assertFalse(patch.containsKey("baseline"))
        assertEquals(1, repo.writes.get())
    }

    @Test fun leavingDirtyProfileRequiresConfirmationAndCancelKeepsDraft() {
        editor()
        rule.onNodeWithTag("profile-translation-profession").performScrollTo().performTextReplacement("Borrador pendiente")
        Espresso.closeSoftKeyboard()
        applyDialogConfiguration()
        rule.onNodeWithContentDescription(strings.getString(R.string.description_icon_back)).performClick()
        capture("discard-dialog")
        rule.waitUntil(5_000) {
            rule.onNodeWithText(strings.getString(R.string.profile_discard_title)).isDisplayed()
        }
        rule.onNodeWithText(strings.getString(R.string.profile_discard_title)).assertIsDisplayed()
        rule.onNodeWithTag("profile-discard-message").assert(hasScrollAction())
        rule.onNodeWithText(strings.getString(R.string.profile_discard_confirm)).assertIsDisplayed()
        rule.onNodeWithText(strings.getString(R.string.profile_keep_editing)).assertIsDisplayed()
        rule.onNodeWithTag("profile-discard-message").performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 10_000f) }
        capture("discard-dialog-message-end")
        rule.onNodeWithText(strings.getString(R.string.profile_keep_editing)).performClick()
        rule.runOnIdle { assertEquals("Borrador pendiente", vm.profileContent.translation("profession")) }
        assertEquals(0, backs.get())
        applyDialogConfiguration()
        rule.onNodeWithContentDescription(strings.getString(R.string.description_icon_back)).performClick()
        rule.onNodeWithText(strings.getString(R.string.profile_discard_confirm)).performClick()
        assertEquals(1, backs.get())
        assertNull(repo.saved)
    }

    private fun capture(name: String) {
        rule.waitForIdle()
        val root = if (name.startsWith("discard-dialog")) rule.onNode(isDialog()) else rule.onRoot()
        val bitmap = root.captureToImage().asAndroidBitmap()
        val directory = File(rule.activity.getExternalFilesDir(null), "phase2-profile-qa").apply { mkdirs() }
        val orientation = rule.activity.resources.configuration.orientation
        File(directory, "$name-$language-dark-$dark-scale-$scale-orientation-$orientation.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
    }

    private fun applyDialogConfiguration() {
        // IME configuration callbacks can reset Activity resources to the device locale.
        @Suppress("DEPRECATION")
        rule.runOnUiThread {
            rule.activity.resources.updateConfiguration(strings.resources.configuration, rule.activity.resources.displayMetrics)
        }
    }

    private class LocalProfileRepository : InfoUserRepository {
        @Volatile var saved: UpdateInfoProfileWrapper? = null
        val writes = AtomicInteger()
        override val myPersonalInfoData = flowOf<PersonalInfoData?>(null)
        override suspend fun updatePersonalInfo(updateInfoProfileWrapper: UpdateInfoProfileWrapper) {
            saved = updateInfoProfileWrapper
            writes.incrementAndGet()
        }
        override suspend fun getPersonalInfo() = Unit
    }
    private object LocalImages : ImageRepository {
        override fun uploadImageProfile(uri: Uri, idUser: String): Flow<StorageTaskResult> = error("No uploads in UI tests")
        override suspend fun compressImg(newImg: Uri): Uri = error("No image picker in UI tests")
    }
}
