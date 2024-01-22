package com.nullpointer.nullsiteadmin.models.credentials.dto

import com.nullpointer.nullsiteadmin.models.credentials.wrapper.CredentialsWrapper

data class CredentialsDTO(
    val email:String,
    val password:String
){
    companion object{
        fun fromCredentialsWrapper(credentialsWrapper: CredentialsWrapper): CredentialsDTO {
            return CredentialsDTO(
                email = credentialsWrapper.email,
                password = credentialsWrapper.password
            )
        }
    }
}