package com.nullpointer.nullsiteadmin.data.remote.auth

import com.nullpointer.nullsiteadmin.models.UserAuth

interface AuthDataSource {
    suspend fun authWithEmailAndPassword(email: String, pass: String): UserAuth
    suspend fun addingTokenUser(
        newToken: String? = null,
        uuidUser: String? = null,
        oldToken: String = ""
    )

    suspend fun getUserToken(): String
    suspend fun logout()
}