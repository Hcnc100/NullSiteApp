package com.nullpointer.nullsiteadmin.ui

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.graphics.asAndroidBitmap
import android.graphics.Bitmap
import java.io.File
import java.lang.reflect.Proxy
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepository
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepository
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.viewModel.EmailDetailsViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.actions.EmailsScreenActions
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.EmailsDetailsScreen
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** No Firebase writes: exercise the production detail screen with local callbacks. */
class EmailDetailsDeleteTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    @Test fun portraitDeleteWaitsForSuccessBeforeLeaving() = deleteWaitsForSuccess(
        Configuration.ORIENTATION_PORTRAIT
    )

    @Test fun landscapeDeleteWaitsForSuccessBeforeLeaving() = deleteWaitsForSuccess(
        Configuration.ORIENTATION_LANDSCAPE
    )

    private fun deleteWaitsForSuccess(orientation: Int) {
        var deletes = 0
        var backs = 0
        val email = EmailData.exampleLong
        val back = { backs++; Unit }
        rule.setContent {
            NullSiteAdminTheme {
                EmailsDetailsScreen(
                    email = email,
                    actionBack = back,
                    snackbarHostState = SnackbarHostState(),
                    emailScreenAction = { action, selected ->
                        assertEquals(EmailsScreenActions.DELETER, action)
                        assertEquals(email, selected)
                        deletes++
                    },
                    orientation = orientation,
                )
            }
        }
        rule.onRoot().captureToImage().asAndroidBitmap().let { bitmap ->
            File(rule.activity.getExternalFilesDir(null), "email-detail-$orientation.png")
                .outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
        rule.onNodeWithContentDescription(rule.activity.getString(R.string.description_deleter_email))
            .performClick()
        rule.runOnIdle {
            assertEquals(0, deletes)
            assertEquals(0, backs)
        }
        rule.onNodeWithText(rule.activity.getString(R.string.email_delete_confirm)).performClick()
        rule.runOnIdle {
            assertEquals(1, deletes)
            // A slow or failed repository call must keep the detail screen alive.
            assertEquals(0, backs)
            // Only the success callback supplied to the ViewModel may navigate back.
            back()
            assertEquals(1, backs)
        }
    }

    @Test fun cancellingConfirmationDoesNotDeleteOrNavigate() {
        var deletes = 0
        var backs = 0
        rule.setContent {
            NullSiteAdminTheme {
                EmailsDetailsScreen(EmailData.exampleLong, { backs++ }, SnackbarHostState(),
                    { _, _ -> deletes++ })
            }
        }
        rule.onNodeWithContentDescription(rule.activity.getString(R.string.description_deleter_email)).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.email_delete_cancel)).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.email_delete_title)).assertDoesNotExist()
        rule.runOnIdle {
            assertEquals(0, deletes)
            assertEquals(0, backs)
        }
    }

    @Test fun deletingDisablesNavigationAndDeleteAndHidesReply() {
        rule.setContent {
            NullSiteAdminTheme {
                EmailsDetailsScreen(EmailData.exampleLong, {}, SnackbarHostState(), { _, _ -> }, isDeleting = true)
            }
        }
        rule.onNodeWithContentDescription(rule.activity.getString(R.string.description_deleter_email)).assertIsNotEnabled()
        rule.onNodeWithContentDescription(rule.activity.getString(R.string.description_icon_back)).assertIsNotEnabled()
        rule.onNodeWithText(rule.activity.getString(R.string.email_reply)).assertDoesNotExist()
    }

    @Test fun spanishDarkDetailsAndConfirmationAreReadable() {
        val configuration = Configuration(rule.activity.resources.configuration).apply {
            setLocale(Locale("es"))
        }
        val context = rule.activity.createConfigurationContext(configuration)
        rule.setContent {
            CompositionLocalProvider(LocalContext provides context, LocalConfiguration provides configuration) {
                NullSiteAdminTheme(darkTheme = true) {
                    EmailsDetailsScreen(
                        EmailData.exampleLong.copy(name = "Remitente de prueba", subject = "Mensaje de prueba", message = "Este es un mensaje local para verificar la interfaz."),
                        {}, SnackbarHostState(), { _, _ -> },
                    )
                }
            }
        }
        rule.onNodeWithText("Responder", useUnmergedTree = true).assertExists()
        rule.onRoot().captureToImage().asAndroidBitmap().let { bitmap ->
            File(rule.activity.getExternalFilesDir(null), "email-detail-es-dark.png")
                .outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
        rule.onNodeWithContentDescription(context.getString(R.string.description_deleter_email)).performClick()
        rule.onNodeWithText("¿Eliminar correo?").assertExists()
        rule.onNode(isDialog()).captureToImage().asAndroidBitmap().let { bitmap ->
            File(rule.activity.getExternalFilesDir(null), "email-confirm-es-dark.png")
                .outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    @Test fun viewModelAllowsOnePendingDeleteAndNavigatesOnlyAfterSuccess() {
        val release = CompletableDeferred<Unit>()
        var calls = 0
        var backs = 0
        val repository = object : EmailsRepository {
            override val listEmails = flowOf(emptyList<EmailData>())
            override suspend fun markAsOpen(emailData: EmailData) = Unit
            override suspend fun requestLastEmail() = 0
            override suspend fun concatenateEmails() = 0
            override suspend fun deleterEmail(idEmail: String) {
                assertEquals("local-test-id", idEmail)
                calls++
                release.await()
            }
        }
        val biometric = Proxy.newProxyInstance(BiometricRepository::class.java.classLoader,
            arrayOf(BiometricRepository::class.java)) { _, method, _ ->
            check(method.name == "isAuthBiometricPassed")
            flowOf(true)
        } as BiometricRepository
        lateinit var vm: EmailDetailsViewModel
        var job: Job? = null
        try {
            rule.runOnIdle {
                vm = EmailDetailsViewModel(repository, biometric)
                job = vm.deleterEmail("local-test-id") { backs++ }
                assertTrue(vm.isDeleting)
                assertNull(vm.deleterEmail("local-test-id") { backs++ })
                assertEquals(0, backs)
            }
        } finally { release.complete(Unit) }
        runBlocking { job?.join() }
        rule.runOnIdle {
            assertFalse(vm.isDeleting)
            assertEquals(1, calls)
            assertEquals(1, backs)
        }
    }

    @Test fun backButtonStillNavigatesWithoutDeleting() {
        var deletes = 0
        var backs = 0
        rule.setContent {
            NullSiteAdminTheme {
                EmailsDetailsScreen(
                    email = EmailData.exampleLong,
                    actionBack = { backs++ },
                    snackbarHostState = SnackbarHostState(),
                    emailScreenAction = { _, _ -> deletes++ },
                )
            }
        }
        rule.onNodeWithContentDescription(rule.activity.getString(R.string.description_icon_back))
            .performClick()
        rule.runOnIdle {
            assertEquals(1, backs)
            assertEquals(0, deletes)
        }
    }
}
