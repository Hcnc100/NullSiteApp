package com.nullpointer.nullsiteadmin.data.remote.auth

import kotlinx.coroutines.flow.Flow

interface AuthDataSource {
    fun isAuthUser(): Flow<Boolean>

    suspend fun authWithEmailAndPassword(email: String, pass: String)
    suspend fun logout()
}