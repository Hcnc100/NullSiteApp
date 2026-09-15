package com.nullpointer.nullsiteadmin.data.auth.remote

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.nullpointer.nullsiteadmin.core.utils.Constants.PHONE_COLLECTION
import com.nullpointer.nullsiteadmin.exception.NullAppException
import com.nullpointer.nullsiteadmin.models.auth.response.AuthResponse
import com.nullpointer.nullsiteadmin.models.credentials.dto.CredentialsDTO
import com.nullpointer.nullsiteadmin.models.phoneInfo.dto.UpdateInfoPhoneDTO
import kotlinx.coroutines.tasks.await

class AuthApiServices {
    private val auth = Firebase.auth
    private val refCollectionPhones = Firebase.firestore.collection(PHONE_COLLECTION)



    suspend fun updateInfoPhone(
        uuidPhone:String,
        updateInfoPhoneDTO: UpdateInfoPhoneDTO
    ){
        val documentExist=refCollectionPhones.document(uuidPhone).get().await()
        when(documentExist.exists()){
            true-> refCollectionPhones.document(uuidPhone).update(updateInfoPhoneDTO.toUpdateMap()).await()
            false -> refCollectionPhones.document(uuidPhone).set(updateInfoPhoneDTO.toCreateMap()).await()
        }
    }


    suspend fun login(credentialsDTO: CredentialsDTO): AuthResponse {
        val response= auth.signInWithEmailAndPassword(
            credentialsDTO.email,
            credentialsDTO.password
        ).await()

        val user = response.user ?: throw NullAppException.AuthException.Authenticated
        val email = user.email ?: throw NullAppException.AuthException.Authenticated
        return AuthResponse(idUser = user.uid, email = email)
    }



     fun logout() {
        auth.signOut()
    }

}
