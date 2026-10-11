package com.nullpointer.nullsiteadmin.ui

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepository
import com.nullpointer.nullsiteadmin.domain.image.ImageRepository
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepository
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepository
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.wrapper.UpdateInfoProfileWrapper
import com.nullpointer.nullsiteadmin.models.credentials.wrapper.CredentialsWrapper
import com.nullpointer.nullsiteadmin.ui.screens.login.viewModel.LoginScreenViewModel
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.viewModel.EditInfoViewModel
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.viewModel.EditProjectViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Proxy
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** Production ViewModels and field delegates, with local repository boundaries only. */
@RunWith(AndroidJUnit4::class)
class FormValidationPolicyTest {
    @Test fun allEditableFieldsAcceptLimitAndRejectOverflowWithoutTruncation() = onMain {
        val project = EditProjectViewModel(SavedStateHandle(), localRepo())
        val profile = EditInfoViewModel(SavedStateHandle(), localRepo(), localRepo())
        val login = LoginScreenViewModel(SavedStateHandle(), localRepo())
        // Literal expectations make unintended policy changes visible.
        val fields = listOf(
            project.nameProject to 100,
            project.descriptionProject to 2000,
            project.urlImgProject to 2048,
            project.urlRepositoryProject to 2048,
            profile.name to 100,
            profile.profession to 120,
            profile.description to 2000,
            login.emailAdmin to 254,
            login.passwordAdmin to 4096,
        )
        fields.forEach { (field, limit) ->
            field.changeValue("a".repeat(limit - 1))
            assertFalse(field.hasError)
            field.changeValue("a".repeat(limit))
            assertFalse(field.hasError)
            assertEquals("$limit/$limit", field.countLength)
            val overflow = "a".repeat(limit + 1)
            field.changeValue(overflow)
            assertTrue(field.hasError)
            assertEquals(overflow, field.currentValue)
            field.changeValue("valid")
            assertFalse(field.hasError)
        }
    }

    @Test fun persistedStorageUrlAllowsEditingAnotherProjectField() {
        val writes = AtomicInteger()
        var job: Job? = null
        onMain {
            val vm = EditProjectViewModel(SavedStateHandle(), localRepo<ProjectRepository>(writes))
            val prefix = "https://firebasestorage.googleapis.com/v0/b/example/o/"
            val url = prefix + "a".repeat(167 - prefix.length)
            val project = ProjectData.example.copy(
                urlImg = url,
                urlRepo = "https://github.com/example/project",
            )
            vm.initVM(project)
            vm.nameProject.changeValue("Updated project")
            job = vm.updatedProject(project) {}
            assertFalse(vm.urlImgProject.hasError)
            assertEquals("167/2048", vm.urlImgProject.countLength)
        }
        runBlocking { job?.join() }
        assertEquals(1, writes.get())
    }

    @Test fun untouchedProfileFieldsBlockPartialSave() = onMain {
        val vm = EditInfoViewModel(SavedStateHandle(), localRepo(), localRepo())
        vm.name.changeValue("Nurse")
        assertNull(vm.validateInfoProfile())
        assertTrue(vm.profession.hasError)
        assertTrue(vm.description.hasError)
    }

    @Test fun whitespaceOnlyProfileIsRejectedWithoutTrimmingUserText() = onMain {
        val vm = EditInfoViewModel(SavedStateHandle(), localRepo(), localRepo())
        vm.name.changeValue("   ")
        vm.profession.changeValue("RN")
        vm.description.changeValue("Description")
        assertNull(vm.validateInfoProfile())
        assertTrue(vm.name.hasError)
        assertEquals("   ", vm.name.currentValue)
    }

    @Test fun untouchedProjectFieldsNeverReachRepository() {
        val writes = AtomicInteger()
        var job: Job? = null
        onMain {
            val vm = EditProjectViewModel(SavedStateHandle(), localRepo<ProjectRepository>(writes))
            vm.nameProject.changeValue("Project")
            job = vm.updatedProject(ProjectData.example) {}
        }
        runBlocking { job?.join() }
        assertEquals("Incomplete form must not be persisted", 0, writes.get())
    }

    @Test fun malformedEmailIsRejectedLocally() = onMain {
        val vm = LoginScreenViewModel(SavedStateHandle(), localRepo<AuthRepository>())
        vm.emailAdmin.changeValue("not-an-email")
        vm.passwordAdmin.changeValue("unchanged password")
        assertNull(vm.getDataAuth())
        assertTrue(vm.emailAdmin.hasError)
    }

    @Test fun credentialPasswordIsNeverTrimmed() = onMain {
        val vm = LoginScreenViewModel(SavedStateHandle(), localRepo<AuthRepository>())
        vm.emailAdmin.changeValue("test@example.com")
        vm.passwordAdmin.changeValue("  secret  ")
        assertEquals("  secret  ", vm.getDataAuth()!!.password)
    }

    @Test fun repeatedProfileSaveCannotStartTwoWrites() {
        val writes = AtomicInteger()
        val release = CountDownLatch(1)
        var first: Job? = null
        try { onMain {
            val vm = EditInfoViewModel(SavedStateHandle(), localRepo<InfoUserRepository>(writes, release), localRepo<ImageRepository>())
            val wrapper = UpdateInfoProfileWrapper("Nurse", null, null, null)
            first = vm.updatePersonalInfo({}, wrapper)
            assertTrue(vm.isUpdatedData)
            assertNull(vm.updatePersonalInfo({}, wrapper))
        } } finally { release.countDown() }
        runBlocking { first?.join() }
        assertEquals(1, writes.get())
    }

    @Test fun repeatedLoginCannotStartTwoRequests() {
        val writes = AtomicInteger()
        val release = CountDownLatch(1)
        var first: Job? = null
        try { onMain {
            val vm = LoginScreenViewModel(SavedStateHandle(), localRepo<AuthRepository>(writes, release))
            val credentials = CredentialsWrapper("test@example.com", "unchanged password")
            first = vm.login(credentials)
            assertTrue(vm.isAuthenticating)
            assertNull(vm.login(credentials))
        } } finally { release.countDown() }
        runBlocking { first?.join() }
        assertEquals(1, writes.get())
    }

    @Test fun loadedProfileKeepsUnchangedFieldsOutOfPartialUpdate() = onMain {
        val vm = EditInfoViewModel(SavedStateHandle(), localRepo<InfoUserRepository>(), localRepo<ImageRepository>())
        vm.initInfoProfile(PersonalInfoData.example)
        assertNull(vm.validateInfoProfile())
        assertFalse(vm.name.hasError || vm.profession.hasError || vm.description.hasError)
        vm.name.changeValue("Updated name")
        val update = vm.validateInfoProfile()!!
        assertEquals("Updated name", update.name)
        assertNull(update.profession)
        assertNull(update.description)
        assertNull(update.uriFileImgProfile)
    }

    @Test fun validProjectStillReachesLocalRepositoryOnce() {
        val writes = AtomicInteger()
        var job: Job? = null
        onMain {
            val vm = EditProjectViewModel(SavedStateHandle(), localRepo<ProjectRepository>(writes))
            val project = ProjectData.example.copy(urlImg = "https://example.com/image.png", urlRepo = "https://example.com/repo")
            vm.initVM(project)
            vm.nameProject.changeValue("Updated project")
            job = vm.updatedProject(project) {}
        }
        runBlocking { job?.join() }
        assertEquals(1, writes.get())
    }

    @Test fun lengthBoundaryErrorsAndValuesSurviveStateRestoration() = onMain {
        val saved = SavedStateHandle()
        fun field() = PropertySavableString("restored", R.string.hint_name_admin,
            R.string.label_name_admin, 50, saved, emptyError = R.string.error_name_admin_empty,
            lengthError = R.string.error_name_admin_length)
        val original = field()
        original.changeValue("a".repeat(50))
        assertFalse(original.hasError)
        original.changeValue("a".repeat(51))
        assertTrue(original.hasError)
        val restored = field()
        assertEquals(original.currentValue, restored.currentValue)
        assertEquals(original.errorValue, restored.errorValue)
        restored.changeValue("valid")
        assertFalse(restored.hasError)
    }

    private fun onMain(block: () -> Unit) =
        InstrumentationRegistry.getInstrumentation().runOnMainSync(block)

    private inline fun <reified T> localRepo(
        writes: AtomicInteger = AtomicInteger(),
        release: CountDownLatch? = null,
    ): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, _ ->
            check(!method.name.startsWith("get")) { "Unexpected repository read: ${method.name}" }
            writes.incrementAndGet()
            check(release?.await(5, TimeUnit.SECONDS) != false) { "Test repository was not released" }
            Unit
        } as T
}
