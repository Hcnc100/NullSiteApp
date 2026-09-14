package com.nullpointer.nullsiteadmin.domain.auth

import com.nullpointer.nullsiteadmin.datasource.auth.local.AuthLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.auth.remote.AuthRemoteDataSource
import com.nullpointer.nullsiteadmin.datasource.infoPhone.local.InfoPhoneLocalDataSource
import com.nullpointer.nullsiteadmin.models.auth.data.AuthData
import com.nullpointer.nullsiteadmin.models.credentials.dto.CredentialsDTO
import com.nullpointer.nullsiteadmin.models.credentials.wrapper.CredentialsWrapper
import com.nullpointer.nullsiteadmin.models.phoneInfo.data.InfoPhoneData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthRepoImplTest {
    @Test
    fun login_saves_remote_auth_data_locally() = runTest {
        val local = FakeAuthLocalDataSource()
        val remote = FakeAuthRemoteDataSource()
        val repository = AuthRepoImpl(local, remote, FakeInfoPhoneDataSource())

        repository.login(CredentialsWrapper("user@example.com", "secret"))

        assertEquals(CredentialsDTO("user@example.com", "secret"), remote.lastCredentials)
        assertEquals(AuthData("user-id", "user@example.com"), local.auth.value)
    }

    @Test
    fun logout_signs_out_remotely_and_clears_local_session() = runTest {
        val local = FakeAuthLocalDataSource().apply { auth.value = AuthData("id", "a@b.com") }
        val remote = FakeAuthRemoteDataSource()
        val repository = AuthRepoImpl(local, remote, FakeInfoPhoneDataSource())

        repository.logout()

        assertEquals(true, remote.loggedOut)
        assertEquals(null, local.auth.value)
    }

    private class FakeAuthLocalDataSource : AuthLocalDataSource {
        val auth = MutableStateFlow<AuthData?>(null)
        override fun getAuthData(): Flow<AuthData?> = auth
        override suspend fun updateAuthData(authData: AuthData) { auth.value = authData }
        override suspend fun deleterAuthData() { auth.value = null }
    }

    private class FakeAuthRemoteDataSource : AuthRemoteDataSource {
        var lastCredentials: CredentialsDTO? = null
        var loggedOut = false
        override suspend fun login(credentialsDTO: CredentialsDTO) = AuthData("user-id", credentialsDTO.email).also { lastCredentials = credentialsDTO }
        override suspend fun updateInfoPhone(uuidPhone: String, updateInfoPhoneDTO: com.nullpointer.nullsiteadmin.models.phoneInfo.dto.UpdateInfoPhoneDTO) = Unit
        override suspend fun logOut() { loggedOut = true }
    }

    private class FakeInfoPhoneDataSource : InfoPhoneLocalDataSource {
        private val empty = InfoPhoneData("", "", "", "", "", "")
        override suspend fun getSavedInfoPhone() = empty
        override suspend fun getCurrentInfoPhone() = empty
        override suspend fun updateSavedData(updateCurrentData: InfoPhoneData) = Unit
        override suspend fun deleterSavedInfoPhone() = Unit
    }
}
