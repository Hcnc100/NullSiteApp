package com.nullpointer.nullsiteadmin.ui

import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.platform.app.InstrumentationRegistry
import com.nullpointer.nullsiteadmin.actions.BiometricResultState
import com.nullpointer.nullsiteadmin.datasource.biometric.local.BiometricDataSource
import com.nullpointer.nullsiteadmin.datasource.settings.local.SettingsLocalDataSource
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepoImpl
import com.nullpointer.nullsiteadmin.models.settings.data.SettingsData
import com.nullpointer.nullsiteadmin.ui.screens.main.BiometricLifecycleEffect
import com.nullpointer.nullsiteadmin.ui.screens.main.BiometricContentGate
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

/** Regression using real DocumentsUI and the production gate; sensor/repositories controlled. */
@RunWith(Parameterized::class)
class BiometricPickerReproductionTest(private val mime: String, private val dark: Boolean, private val flow: String) {
    companion object {
        @JvmStatic @Parameterized.Parameters(name = "mime={0}-dark={1}-flow={2}")
        fun cases() = listOf("application/pdf" to "single", "image/png" to "single",
            "image/png" to "multiple", "image/png" to "crop").flatMap { (mime, flow) ->
            listOf(false,true).map { arrayOf(mime,it,flow) }
        }
    }
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()
    @Test fun pickerReturnLocksButRetainsSelectionAndNavigationUntilUnlock() {
        val settings = object : SettingsLocalDataSource {
            val data = MutableStateFlow<SettingsData?>(SettingsData(isBiometricEnabled = true))
            override fun getSettingsData() = data
            override suspend fun saveSettingsData(settingsData: SettingsData) { data.value = settingsData }
            override suspend fun clearData() { data.value = null }
            override suspend fun changeBiometricEnabled(isEnable: Boolean) { data.value = data.value!!.copy(isBiometricEnabled = isEnable) }
            override suspend fun changeTimeOutLocked(timeOut: Long) { data.value = data.value!!.copy(timeOutLock = timeOut) }
        }
        var cancelAuthentication = false
        val biometric = object : BiometricDataSource {
            override fun checkBiometricSupport() = true
            override suspend fun enableFingerBiometric() = BiometricResultState.PASSED
            override suspend fun unlockByFingerBiometric(): BiometricResultState {
                if (cancelAuthentication) throw kotlinx.coroutines.CancellationException("QA user cancellation")
                return BiometricResultState.PASSED
            }
        }
        val repo = BiometricRepoImpl(settings, biometric)
        val events = mutableListOf<String>()
        var mounts = 0
        var disposals = 0
        var callbacks = 0
        var processed = 0
        var selectedUri: String? = null
        var croppedUri: android.net.Uri? = null
        var sourceUri: String? = null
        var selectedCount = 0
        val name = "qa-${System.nanoTime().toString().takeLast(8)}." + if (mime == "application/pdf") "pdf" else "png"
        runBlocking { repo.initVerifyBiometrics(); repo.unlockByBiometric(); assertEquals(true,repo.isAuthBiometricPassed.first()) }
        val resolver = rule.activity.contentResolver
        val collection = if (mime == "application/pdf") MediaStore.Downloads.EXTERNAL_CONTENT_URI else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val file = resolver.insert(collection, ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,name)
            put(MediaStore.MediaColumns.MIME_TYPE,mime)
            put(MediaStore.MediaColumns.RELATIVE_PATH,if (mime == "application/pdf") Environment.DIRECTORY_DOWNLOADS else Environment.DIRECTORY_PICTURES)
        })!!
        resolver.openOutputStream(file)!!.use {
            if (mime == "application/pdf") it.write("%PDF-1.4\nQA only".toByteArray())
            else android.graphics.Bitmap.createBitmap(8,8,android.graphics.Bitmap.Config.ARGB_8888).let { bitmap ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it); bitmap.recycle()
            }
        }
        val secondName = name.replace(".png", "-second.png")
        val secondFile = if (flow == "multiple") resolver.insert(collection, ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, secondName)
            put(MediaStore.MediaColumns.MIME_TYPE, mime)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        })!!.also { uri -> resolver.openInputStream(file)!!.use { input ->
            resolver.openOutputStream(uri)!!.use { output -> input.copyTo(output) }
        } } else null
        try {
            rule.setContent {
                var initialResume by remember { mutableStateOf(true) }
                BiometricLifecycleEffect(onPause = { repo.blockBiometric() }, onResume = {
                        if (initialResume) initialResume = false
                        else { events.add("return: ON_RESUME -> initVerifyBiometrics"); runBlocking { repo.initVerifyBiometrics() } }
                })
                val unlocked by repo.isAuthBiometricPassed.collectAsState(initial = true)
                NullSiteAdminTheme(darkTheme = dark) {
                BiometricContentGate(locked = unlocked != true, lockContent = {
                    Column { Text("QA LOCKED"); Button(onClick = { runBlocking { repo.unlockByBiometric() } }) { Text("QA UNLOCK") } }
                }) {
                    val nav = rememberNavController()
                    NavHost(navController = nav, startDestination = "editor") { composable("editor") {
                    var pending by rememberSaveable { mutableStateOf<String?>(null) }
                    DisposableEffect(Unit) { mounts++; onDispose { disposals++ } }
                    fun receive(uri: android.net.Uri?) {
                        callbacks++; pending = uri?.toString(); selectedUri = pending
                        events.add("picker callback: ${if (uri != null) "file selected" else "cancelled"}")
                    }
                    val crop = rememberLauncherForActivityResult(com.canhub.cropper.CropImageContract()) { result ->
                        assertTrue("Crop must complete successfully", result.isSuccessful)
                        croppedUri = result.uriContent
                        receive(result.uriContent)
                    }
                    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                        sourceUri = uri?.toString(); selectedCount = if (uri == null) 0 else 1
                        if (flow == "crop" && uri != null) crop.launch(com.canhub.cropper.CropImageContractOptions(
                            uri, com.canhub.cropper.CropImageOptions(imageSourceIncludeGallery = false,
                                imageSourceIncludeCamera = false, fixAspectRatio = true)))
                        else receive(uri)
                    }
                    val multiple = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
                        sourceUri = uris.firstOrNull()?.toString(); selectedCount = uris.size
                        receive(uris.firstOrNull())
                    }
                    Column {
                        Text("QA EDITOR")
                        Button(onClick = {
                            if (flow == "multiple") multiple.launch(arrayOf(mime)) else picker.launch(arrayOf(mime))
                        }) { Text("Open QA picker") }
                        if (pending != null) { Text("QA FILE RETAINED"); Button(onClick = { processed++; pending = null }) { Text("QA PROCESS") } }
                    }
                    } }
                }
                }
            }
            rule.onNodeWithText("Open QA picker").performClick()
            val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
            fun back() {
                val time = android.os.SystemClock.uptimeMillis()
                assertTrue(automation.injectInputEvent(android.view.KeyEvent(time,time,android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_BACK,0),true))
                assertTrue(automation.injectInputEvent(android.view.KeyEvent(time,time+1,android.view.KeyEvent.ACTION_UP,android.view.KeyEvent.KEYCODE_BACK,0),true))
            }
            fun find(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo? {
                if (node == null) return null
                fun matches(value: CharSequence?) = if (text == "Select") value?.toString()?.equals(text,true) == true
                    else value?.toString()?.contains(text,true) == true
                if (matches(node.text) || matches(node.contentDescription)) return node
                for (i in 0 until node.childCount) find(node.getChild(i),text)?.let { return it }
                return null
            }
            fun click(text: String): Boolean {
                val node = find(automation.rootInActiveWindow,text) ?: return false
                var actionNode: AccessibilityNodeInfo? = if (mime == "image/png" && text == name) null else node
                while (actionNode != null) {
                    if (actionNode.isClickable && actionNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return true
                    actionNode = actionNode.parent
                }
                val bounds = android.graphics.Rect(); node.getBoundsInScreen(bounds)
                if (bounds.isEmpty) return false
                android.os.ParcelFileDescriptor.AutoCloseInputStream(
                    automation.executeShellCommand("input tap ${bounds.centerX()} ${bounds.centerY()}")
                ).use { it.readBytes() }
                return true
            }
            fun longClick(text: String): Boolean {
                val node = find(automation.rootInActiveWindow, text) ?: return false
                var actionNode: AccessibilityNodeInfo? = node
                while (actionNode != null) {
                    if (actionNode.isLongClickable && actionNode.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)) return true
                    actionNode = actionNode.parent
                }
                val bounds = android.graphics.Rect(); node.getBoundsInScreen(bounds)
                if (bounds.isEmpty) return false
                android.os.ParcelFileDescriptor.AutoCloseInputStream(automation.executeShellCommand(
                    "input swipe ${bounds.centerX()} ${bounds.centerY()} ${bounds.centerX()} ${bounds.centerY()} 700"
                )).use { it.readBytes() }
                return true
            }
            if (mime == "image/png") {
                // Grid thumbnail parents advertise click without selecting a document.
                // DocumentsUI list mode exposes the actual selectable filename row.
                click("List view"); Thread.sleep(300)
            }
            var multipleStarted = false
            var secondSelected = false
            val end = System.currentTimeMillis() + 25000
            while (callbacks == 0 && System.currentTimeMillis() < end) {
                val tapped = if (flow == "multiple") {
                    if (!multipleStarted) {
                        val pressed = longClick(name)
                        Thread.sleep(500)
                        multipleStarted = find(automation.rootInActiveWindow, "selected") != null
                        pressed
                    }
                    else if (!secondSelected) { secondSelected = click(secondName); secondSelected }
                    else click("Select")
                } else if (flow == "crop" && sourceUri != null) click("Crop") else click(name)
                if (!tapped) {
                    if (mime == "application/pdf") click("Downloads")
                    else click("Pictures")
                }
                Thread.sleep(500) // Wait for provider navigation/thumbnail layout and real result delivery.
            }
            val selected = callbacks == 1
            if (!selected) {
                val labels = mutableListOf<String>()
                fun dump(node: AccessibilityNodeInfo?) {
                    if (node == null) return
                    if (node.text != null || node.contentDescription != null) {
                        val bounds = android.graphics.Rect(); node.getBoundsInScreen(bounds)
                        labels.add("${node.className}: ${node.text} / ${node.contentDescription}; bounds=$bounds; clickable=${node.isClickable}; long=${node.isLongClickable}")
                    }
                    for (i in 0 until node.childCount) dump(node.getChild(i))
                }
                dump(automation.rootInActiveWindow)
                File(rule.activity.getExternalFilesDir(null),"picker-qa-labels.txt").writeText(labels.joinToString("\n"))
            }
            assertTrue("QA file must be selected through actual DocumentsUI",selected)
            try { rule.waitUntil(5000) { runBlocking { repo.isAuthBiometricPassed.first() } == false } }
            catch (error: Throwable) {
                val labels = mutableListOf<String>()
                fun inspect(node: AccessibilityNodeInfo?) {
                    if (node == null) return
                    if (node.text != null || node.contentDescription != null) labels.add("${node.className}: ${node.text} / ${node.contentDescription}; clickable=${node.isClickable}; enabled=${node.isEnabled}")
                    for (i in 0 until node.childCount) inspect(node.getChild(i))
                }
                inspect(automation.rootInActiveWindow)
                find(automation.rootInActiveWindow,name)?.let { node ->
                    val rect = android.graphics.Rect(); node.getBoundsInScreen(rect); labels.add("FILE BOUNDS $rect")
                }
                automation.takeScreenshot()?.let { bitmap ->
                    File(rule.activity.getExternalFilesDir(null),"picker-qa-after-tap.png").outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it) }; bitmap.recycle()
                }
                File(rule.activity.getExternalFilesDir(null),"picker-qa-after-tap.txt").writeText("name=$name; events=$events; callbacks=$callbacks\n"+labels.joinToString("\n"))
                throw error
            }
            rule.onNodeWithText("QA LOCKED").assertIsDisplayed()
            rule.onNodeWithText("QA EDITOR").assertDoesNotExist()
            // The real biometric source cancels its coroutine when the user cancels.
            cancelAuthentication = true
            val cancelled = runCatching { runBlocking { repo.unlockByBiometric() } }.exceptionOrNull()
            assertTrue(cancelled is kotlinx.coroutines.CancellationException)
            cancelAuthentication = false
            assertEquals(false, runBlocking { repo.isAuthBiometricPassed.first() })
            rule.onNodeWithText("QA LOCKED").assertIsDisplayed()
            back()
            Thread.sleep(600) // Global back is asynchronous; finish key dispatch before unlocking.
            rule.onNodeWithText("QA LOCKED").assertIsDisplayed()
            assertTrue(events.any { it.contains("return: ON_RESUME") })
            rule.runOnIdle { assertEquals(1,mounts); assertEquals(0,disposals); assertEquals(1,callbacks); assertNotNull(selectedUri); assertEquals(0,processed) }
            assertEquals(if (flow == "multiple") 2 else 1, selectedCount)
            resolver.query(android.net.Uri.parse(sourceUri),arrayOf(android.provider.OpenableColumns.DISPLAY_NAME),null,null,null)!!.use {
                assertTrue(it.moveToFirst()); assertEquals(name,it.getString(0))
            }
            rule.onNodeWithText("QA UNLOCK").performClick()
            rule.onNodeWithText("QA FILE RETAINED").assertIsDisplayed()
            rule.onNodeWithText("QA PROCESS").performClick()
            rule.runOnIdle { assertEquals(1,processed); assertEquals(1,mounts); assertEquals(0,disposals) }
            // Cancelling another real picker must not manufacture or process a selection.
            rule.onNodeWithText("Open QA picker").performClick()
            val cancelEnd = System.currentTimeMillis() + 10000
            while (callbacks < 2 && System.currentTimeMillis() < cancelEnd) {
                if (automation.rootInActiveWindow?.packageName?.toString()?.contains("documentsui") == true) {
                    back()
                }
                Thread.sleep(500)
            }
            rule.waitUntil(5000) { callbacks == 2 && runBlocking { repo.isAuthBiometricPassed.first() } == false }
            rule.onNodeWithText("QA LOCKED").assertIsDisplayed()
            rule.onNodeWithText("QA UNLOCK").performClick()
            rule.onNodeWithText("QA FILE RETAINED").assertDoesNotExist()
            // A real background/resume still locks; preserving state is not a bypass.
            rule.runOnUiThread { rule.activity.startActivity(android.content.Intent(android.provider.Settings.ACTION_SETTINGS)) }
            rule.waitUntil(5000) { !rule.activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) }
            assertNull(runBlocking { repo.isAuthBiometricPassed.first() })
            rule.runOnUiThread { rule.activity.startActivity(android.content.Intent(rule.activity,ComponentActivity::class.java)
                .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)) }
            rule.waitUntil(5000) { runBlocking { repo.isAuthBiometricPassed.first() } == false }
            rule.onNodeWithText("QA LOCKED").assertIsDisplayed()
            rule.runOnIdle { assertEquals(1,processed); assertEquals(1,mounts); assertEquals(0,disposals); assertNull(selectedUri) }
            rule.onNodeWithText("QA UNLOCK").performClick()
            if (flow == "crop" && dark) {
                fun shell(command: String) {
                    android.os.ParcelFileDescriptor.AutoCloseInputStream(
                        automation.executeShellCommand(command)).use { it.readBytes() }
                }
                fun returnToEditor() {
                    rule.runOnUiThread { rule.activity.startActivity(android.content.Intent(rule.activity,ComponentActivity::class.java)
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)) }
                    rule.waitUntil(10000) { runBlocking { repo.isAuthBiometricPassed.first() } == false }
                    rule.onNodeWithText("QA LOCKED").assertIsDisplayed()
                    rule.runOnIdle { assertEquals(1,mounts); assertEquals(0,disposals); assertEquals(1,processed) }
                    rule.onNodeWithText("QA UNLOCK").performClick()
                }
                shell("input keyevent KEYCODE_HOME")
                rule.waitUntil(10000) { !rule.activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) }
                assertNull(runBlocking { repo.isAuthBiometricPassed.first() })
                returnToEditor()
                try {
                    shell("input keyevent KEYCODE_SLEEP")
                    rule.waitUntil(10000) { !rule.activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) }
                    assertNull(runBlocking { repo.isAuthBiometricPassed.first() })
                } finally {
                    shell("input keyevent KEYCODE_WAKEUP")
                    shell("wm dismiss-keyguard")
                }
                returnToEditor()
            }
            val output = File(rule.activity.getExternalFilesDir(null),"biometric-picker-reproduction.txt")
            output.writeText(events.joinToString("\n") + "\nResult: $mime dark=$dark flow=$flow; locked, back blocked, NavHost/editor retained, result delivered once, processed once after unlock.\n")
        } finally {
            resolver.delete(file,null,null)
            secondFile?.let { resolver.delete(it,null,null) }
            croppedUri?.let { runCatching { resolver.delete(it,null,null) } }
        }
    }
}
