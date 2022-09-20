package com.nullpointer.nullsiteadmin.data.remote.auth

import com.nullpointer.nullsiteadmin.models.UserAuth

interface AuthDataSource {
    suspend fun authWithEmailAndPassword(email: String, pass: String): UserAuth
    suspend fun updateTokenUser(token: String? = null, uuidUser: String? = null)
    suspend fun getUserToken(): String
    suspend fun logout()
}