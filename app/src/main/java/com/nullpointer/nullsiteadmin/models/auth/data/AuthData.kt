package com.nullpointer.nullsiteadmin.models.auth.data

import com.nullpointer.nullsiteadmin.models.auth.response.AuthResponse
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    val id:String,
    val email:String,
    val tokenGCM:String ="",
){
    companion object{
        fun fromAuthResponse(authResponse: AuthResponse): AuthData {
            return  AuthData(
                id = authResponse.idUser,
                email = authResponse.email,
            )
        }
    }
}
