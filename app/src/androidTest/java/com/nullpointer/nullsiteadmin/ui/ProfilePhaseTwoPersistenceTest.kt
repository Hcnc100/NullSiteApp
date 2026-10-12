package com.nullpointer.nullsiteadmin.ui

import android.net.Uri
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.test.platform.app.InstrumentationRegistry
import com.nullpointer.nullsiteadmin.core.states.StorageTaskResult
import com.nullpointer.nullsiteadmin.data.user.local.UserDataStore
import com.nullpointer.nullsiteadmin.domain.image.ImageRepository
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepository
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileSocialLink
import com.nullpointer.nullsiteadmin.models.personalInfo.wrapper.UpdateInfoProfileWrapper
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.viewModel.EditInfoViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.UUID

class ProfilePhaseTwoPersistenceTest {
    private val profile = PersonalInfoData("QA", "https://example.com/profile.png", 1, "Developer", "Biography")

    @Test fun legacyAndEnrichedProfilesPersistAcrossDataStoreInstances() = runBlocking {
        val file = File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir,
            "profile-qa-${UUID.randomUUID()}.preferences_pb")
        val firstJob = SupervisorJob()
        val secondJob = SupervisorJob()
        try {
            val first = PreferenceDataStoreFactory.create(scope = CoroutineScope(firstJob + Dispatchers.IO)) { file }
            first.edit {
                it[stringPreferencesKey("KEY_USER_DATA")] = """{"name":"Old","urlImg":"","updateAt":1,"profession":"Dev","description":"Bio","futureField":true}"""
            }
            val local = UserDataStore(first)
            assertNull(local.getPersonalInfo().first()!!.socialLinks)
            assertTrue(local.getPersonalInfo().first()!!.translations.isEmpty())
            val enriched = profile.copy(socialLinks = listOf(ProfileSocialLink("github", "https://github.com/qa")),
                translations = mapOf("es" to mapOf("profession" to "Desarrollador"), "en" to mapOf("description" to "English")))
            local.updatePersonalInfo(enriched)
            firstJob.cancelAndJoin()
            val second = PreferenceDataStoreFactory.create(scope = CoroutineScope(secondJob + Dispatchers.IO)) { file }
            assertEquals(enriched, UserDataStore(second).getPersonalInfo().first())
        } finally {
            firstJob.cancelAndJoin()
            secondJob.cancelAndJoin()
            file.delete()
        }
    }

    @Test fun restoredProfileEditorKeepsOriginalBaselineAndUnsavedContent() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val saved = SavedStateHandle()
            val first = EditInfoViewModel(saved, NoWrites, NoImages)
            first.initInfoProfile(profile)
            first.name.changeValue("Draft name")
            first.profileContent.selectLocale("en")
            first.profileContent.changeTranslation("description", "Draft translation")
            first.profileContent.changeSocialUrl("github", "https://github.com/draft")
            val restored = EditInfoViewModel(SavedStateHandle(saved.keys().associateWith { saved.get<Any?>(it) }), NoWrites, NoImages)
            restored.initInfoProfile(profile.copy(name = "Changed remotely"))
            assertEquals("Draft name", restored.name.currentValue)
            assertEquals("Draft translation", restored.profileContent.translation("description"))
            val request = restored.validateInfoProfile()!!
            assertEquals(profile, request.baseline)
            assertEquals("Draft name", request.name)
            assertNotNull(request.socialLinks)
            assertNotNull(request.translations)
            assertTrue(restored.hasUnsavedChanges)
        }
    }

    private object NoWrites : InfoUserRepository {
        override val myPersonalInfoData = flowOf<PersonalInfoData?>(null)
        override suspend fun updatePersonalInfo(updateInfoProfileWrapper: UpdateInfoProfileWrapper) = error("No remote writes")
        override suspend fun getPersonalInfo() = Unit
    }
    private object NoImages : ImageRepository {
        override fun uploadImageProfile(uri: Uri, idUser: String): Flow<StorageTaskResult> = error("No uploads")
        override suspend fun compressImg(newImg: Uri): Uri = error("No images")
    }
}
