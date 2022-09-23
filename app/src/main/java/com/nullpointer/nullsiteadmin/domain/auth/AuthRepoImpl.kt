package com.nullpointer.nullsiteadmin.domain.auth

import com.nullpointer.nullsiteadmin.data.local.settings.SettingsDataSource
import com.nullpointer.nullsiteadmin.data.remote.auth.AuthDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AuthRepoImpl(
    private val authDataSource: AuthDataSource,
    private val settingsDataSource: SettingsDataSource,
) : AuthRepository {


    override val isUserAuth: Flow<Boolean> = settingsDataSource.isAuthUser()

    override suspend fun updateTokenUser(token: String) {
        authDataSource.addingTokenUser(token)
        settingsDataSource.updateTokenMsg(token)
    }

    override suspend fun authUserWithEmailAndPassword(email: String, password: String) {
        val userResponse = authDataSource.authWithEmailAndPassword(email, password)
        settingsDataSource.saveUserAuth(userResponse)
    }

    override suspend fun logout() {
        authDataSource.logout()
    }

    override suspend fun verifyTokenMessaging(): Boolean {
        val localToken = settingsDataSource.getUserAuth().first().tokenMsg
        val currentToken = authDataSource.getUserToken()
        if (localToken != currentToken) {
            authDataSource.addingTokenUser(currentToken)
            settingsDataSource.updateTokenMsg(currentToken)
        }
        return localToken != currentToken
    }
}