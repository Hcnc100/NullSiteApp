package com.nullpointer.nullsiteadmin.ui

import android.graphics.Bitmap
import android.content.res.Configuration
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.toArgb
import com.nullpointer.nullsiteadmin.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.platform.app.InstrumentationRegistry
import com.nullpointer.nullsiteadmin.ui.theme.AppShapes
import com.nullpointer.nullsiteadmin.ui.theme.AppTypography
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class MaterialThemeContractTest {
    @get:Rule val rule = createComposeRule()

    @Test fun lightThemeContract() = checkTheme(false)
    @Test fun darkThemeContract() = checkTheme(true)

    private fun checkTheme(dark: Boolean) {
        rule.setContent {
            NullSiteAdminTheme(darkTheme = dark) {
                val scheme = MaterialTheme.colorScheme
                val context = LocalContext.current
                val nativeConfig = Configuration(context.resources.configuration).apply {
                    uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
                        if (dark) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
                }
                val themed = ContextThemeWrapper(context.createConfigurationContext(nativeConfig), R.style.Theme_MySplash)
                val nativeColor = TypedValue()
                assertTrue(themed.theme.resolveAttribute(androidx.core.splashscreen.R.attr.windowSplashScreenBackground, nativeColor, true))
                assertEquals("Native splash must follow the Compose background in both modes", scheme.background.toArgb(), nativeColor.data)
                val pairs = listOf(
                    "primary" to (scheme.primary to scheme.onPrimary),
                    "primaryContainer" to (scheme.primaryContainer to scheme.onPrimaryContainer),
                    "secondary" to (scheme.secondary to scheme.onSecondary),
                    "secondaryContainer" to (scheme.secondaryContainer to scheme.onSecondaryContainer),
                    "tertiary" to (scheme.tertiary to scheme.onTertiary),
                    "tertiaryContainer" to (scheme.tertiaryContainer to scheme.onTertiaryContainer),
                    "background" to (scheme.background to scheme.onBackground),
                    "surfaceVariant" to (scheme.surfaceVariant to scheme.onSurfaceVariant),
                    "error" to (scheme.error to scheme.onError),
                    "errorContainer" to (scheme.errorContainer to scheme.onErrorContainer),
                    "inverseSurface" to (scheme.inverseSurface to scheme.inverseOnSurface),
                ) + listOf(
                    scheme.surface, scheme.surfaceBright, scheme.surfaceDim,
                    scheme.surfaceContainerLowest, scheme.surfaceContainerLow,
                    scheme.surfaceContainer, scheme.surfaceContainerHigh, scheme.surfaceContainerHighest,
                ).mapIndexed { index, color -> "surface-$index" to (color to scheme.onSurface) }
                pairs.forEach { (name, colors) ->
                    assertTrue("$name contrast must be >=4.5:1", contrast(colors.first, colors.second) >= 4.5f)
                }
                assertTrue("outline must contrast with surface", contrast(scheme.outline, scheme.surface) >= 3f)
                assertEquals(16.sp, MaterialTheme.typography.bodyLarge.fontSize)
                assertEquals(24.sp, MaterialTheme.typography.bodyLarge.lineHeight)
                assertEquals(0.5.sp, MaterialTheme.typography.bodyLarge.letterSpacing)
                assertEquals(AppTypography.bodyLarge, MaterialTheme.typography.bodyLarge)
                assertEquals(AppShapes.medium, MaterialTheme.shapes.medium)
                Surface(Modifier.fillMaxSize()) {
                    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("NullSite", style = MaterialTheme.typography.headlineMedium)
                        Text("Material 3 · " + if (dark) "Oscuro" else "Claro",
                            style = MaterialTheme.typography.titleMedium)
                        Card {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Proyectos", style = MaterialTheme.typography.titleLarge)
                                Text("Superficies suaves, fuente del sistema y contraste accesible.",
                                    style = MaterialTheme.typography.bodyLarge)
                                Button(onClick = {}) { Text("Editar proyecto") }
                            }
                        }
                    }
                }
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("NullSite").assertIsDisplayed()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        // PixelCopy through Compose waits for rendering; UiAutomation could capture
        // an empty Activity frame immediately after setContent on this emulator.
        val image = rule.onRoot().captureToImage().asAndroidBitmap()
        val dir = File(instrumentation.targetContext.filesDir, "phase3-qa").apply { mkdirs() }
        val orientation = instrumentation.targetContext.resources.configuration.orientation
        File(dir, "material3-dark-$dark-orientation-$orientation.png").outputStream().use {
            image.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        image.recycle()
    }

    private fun contrast(a: Color, b: Color): Float {
        val x = a.luminance()
        val y = b.luminance()
        return (maxOf(x, y) + 0.05f) / (minOf(x, y) + 0.05f)
    }
}
