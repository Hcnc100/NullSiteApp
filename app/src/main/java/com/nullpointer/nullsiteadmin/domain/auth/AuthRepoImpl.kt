package com.nullpointer.nullsiteadmin.domain.auth

import com.nullpointer.nullsiteadmin.data.remote.auth.AuthDataSource
import kotlinx.coroutines.flow.Flow

class AuthRepoImpl(
    private val authDataSource: AuthDataSource
) : AuthRepository {
    override val isUserAuth: Flow<Boolean> =
        authDataSource.isAuthUser()

    override suspend fun authUserWithEmailAndPassword(email: String, password: String) =
        authDataSource.authWithEmailAndPassword(email, password)
}