package com.nullpointer.nullsiteadmin.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isUserAuth: Flow<Boolean>
    suspend fun updateTokenUser(token: String? = null, uuidUser: String?=null)
    suspend fun authUserWithEmailAndPassword(email: String, password: String)
    suspend fun logout()
}