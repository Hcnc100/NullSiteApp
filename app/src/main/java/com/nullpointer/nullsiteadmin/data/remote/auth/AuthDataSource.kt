package com.nullpointer.nullsiteadmin.data.remote.auth

import kotlinx.coroutines.flow.Flow

interface AuthDataSource {
    suspend fun authWithEmailAndPassword(email: String, pass: String)
    suspend fun updateTokenUser(token: String? = null, uuidUser: String?=null)
    suspend fun logout()
    fun isAuthUser(): Flow<Boolean>
}