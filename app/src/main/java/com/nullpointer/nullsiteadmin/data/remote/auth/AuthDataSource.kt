package com.nullpointer.nullsiteadmin.data.remote.auth

import kotlinx.coroutines.flow.Flow

interface AuthDataSource {
    suspend fun authWithEmailAndPassword(email: String, pass: String)
    fun isAuthUser(): Flow<Boolean>
}