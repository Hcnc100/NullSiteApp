package com.nullpointer.nullsiteadmin.ui

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import coil.Coil
import coil.ImageLoader
import coil.fetch.Fetcher
import coil.fetch.DrawableResult
import coil.decode.DataSource
import coil.request.Options
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.lifecycle.SavedStateHandle
import androidx.test.espresso.Espresso
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.project.data.*
import com.nullpointer.nullsiteadmin.models.project.dto.UpdateProjectDTO
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.EditProjectScreen
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.viewModel.EditProjectViewModel
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import com.ramcosta.composedestinations.spec.Direction
import android.net.Uri
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.util.Locale

@RunWith(Parameterized::class)
class ProjectPhaseThreeUiTest(private val dark: Boolean, private val language: String, private val scale: Float) {
    companion object {
        @JvmStatic @Parameterized.Parameters(name = "dark={0}-language={1}-scale={2}")
        fun cases() = listOf(arrayOf(false,"es",1f), arrayOf(false,"en",2f), arrayOf(true,"es",2f), arrayOf(true,"en",1f))
    }
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()
    private var originalConfig: Configuration? = null
    private var originalImageLoader: ImageLoader? = null
    @After fun restoreResources() {
        originalImageLoader?.let(Coil::setImageLoader)
        originalConfig?.let { config ->
        @Suppress("DEPRECATION")
        rule.runOnUiThread { rule.activity.resources.updateConfiguration(config, rule.activity.resources.displayMetrics) }
    } }

    @Test fun enrichedEditorReordersBilingualMetadataRejectsInvalidUrlAndSavesExplicitPublication() {
        val repo = ProjectQaRepository(); lateinit var vm: EditProjectViewModel
        val a = ProjectImage("https://example.invalid/a.png", "Capture A", translations = mapOf("es" to mapOf("alt" to "Captura A"), "en" to mapOf("alt" to "Screen A")))
        val b = ProjectImage("https://example.invalid/b.png", "Capture B", order = 1)
        val project = ProjectData("qa", "Project QA", "Description", "https://example.invalid/cover.png", isVisible = true,
            content = ProjectContent(urlPlay = "https://example.invalid/demo", gallery = listOf(a.url,b.url), galleryItems = listOf(a,b),
                translations = mapOf("es" to mapOf("name" to "Proyecto"), "en" to mapOf("name" to "Project"))))
        originalConfig = Configuration(rule.activity.resources.configuration)
        val config = Configuration(originalConfig).apply { setLocale(Locale(language)); fontScale = scale }
        @Suppress("DEPRECATION")
        rule.runOnUiThread { rule.activity.resources.updateConfiguration(config, rule.activity.resources.displayMetrics); vm = EditProjectViewModel(SavedStateHandle(),repo) }
        val strings = rule.activity.createConfigurationContext(config)
        originalImageLoader = Coil.imageLoader(rule.activity)
        Coil.setImageLoader(ImageLoader.Builder(rule.activity).components {
            add(object : Fetcher.Factory<Uri> {
                override fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher? {
                    if (data.host != "example.invalid") return null
                    return Fetcher {
                        val bitmap = Bitmap.createBitmap(320,180,Bitmap.Config.ARGB_8888)
                        Canvas(bitmap).apply {
                            drawColor(Color.rgb(0,105,115))
                            drawText(data.lastPathSegment.orEmpty(),20f,95f,Paint().apply { color = Color.WHITE; textSize = 30f; isAntiAlias = true })
                        }
                        DrawableResult(BitmapDrawable(rule.activity.resources,bitmap),false,DataSource.MEMORY)
                    }
                }
            })
        }.build())
        rule.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides rule.activity, LocalContext provides strings,
                LocalConfiguration provides config, LocalDensity provides Density(LocalDensity.current.density,scale)) {
                NullSiteAdminTheme(darkTheme = dark) {
                    EditProjectScreen(project, object : ActionRootDestinations {
                        override fun backDestination() = true
                        override fun changeRoot(direction: Direction) = error("No navigation")
                        override fun changeRoot(route: Uri) = error("No navigation")
                    }, editProjectViewModel = vm)
                }
            }
        }
        rule.onNodeWithTag("project-demo").performScrollTo().performTextReplacement("http://example.invalid/demo")
        Espresso.closeSoftKeyboard()
        rule.runOnIdle { assertFalse(vm.isDataValid) }
        rule.onNodeWithTag("project-content-error").performScrollTo().assertIsDisplayed()
        rule.onNodeWithTag("project-demo").performScrollTo().performTextReplacement("https://example.invalid/new-demo")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithTag("profile-locale-en").performScrollTo().performClick()
        rule.onNodeWithTag("project-translation-name").performScrollTo().performTextReplacement("English project edited")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithTag("project-capture-translated-alt-0").performScrollTo().performTextReplacement("Updated screenshot A")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithTag("project-down-0").performScrollTo().performClick()
        rule.runOnIdle {
            assertEquals(b.url, vm.contentEditor.images[0].url)
            assertEquals("Updated screenshot A", vm.contentEditor.images[1].translations["en"]?.get("alt"))
            assertEquals("Captura A", vm.contentEditor.images[1].translations["es"]?.get("alt"))
        }
        rule.onNodeWithTag("project-capture-alt-1").performScrollTo().assertTextContains("Capture A")
        rule.waitForIdle()
        rule.onRoot().captureToImage().asAndroidBitmap().let { bitmap ->
            val dir = File(rule.activity.getExternalFilesDir(null), "phase3-project-qa").apply { mkdirs() }
            File(dir,"gallery-$language-dark-$dark-font-$scale-orientation-${config.orientation}.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG,100,it) }
            bitmap.recycle()
        }
        rule.onNodeWithTag("project-published").performScrollTo().performClick()
        rule.runOnIdle { assertFalse(vm.published); assertTrue(vm.isDataValid) }
        rule.onNodeWithContentDescription(strings.getString(R.string.description_icon_back)).performClick()
        rule.onNodeWithText(strings.getString(R.string.project_discard_title)).assertExists()
        rule.onNodeWithText(strings.getString(R.string.profile_keep_editing)).performClick()
        val saveButton = rule.onNodeWithText(strings.getString(R.string.text_update_project))
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) saveButton.performScrollTo()
        saveButton.assertIsDisplayed().performClick()
        rule.waitUntil(5000) { repo.saved != null }
        val request = repo.saved!!
        assertEquals(project,request.baseline)
        assertEquals(false,request.isVisible)
        assertNull(request.name); assertNull(request.urlRepo)
        val patch = UpdateProjectDTO.fromUpdateProjectWrapper(request).toUpdateMap()
        assertEquals(false,patch["published"])
        assertEquals("English project edited",patch["translations.en.name"])
        assertEquals(listOf(b.url,a.url),patch["gallery"])
        assertFalse(patch.containsKey("baseline"))
    }
}
