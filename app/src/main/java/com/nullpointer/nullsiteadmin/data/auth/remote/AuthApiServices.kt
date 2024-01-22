package com.nullpointer.nullsiteadmin.data.auth.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nullpointer.nullsiteadmin.core.utils.Constants.PHONE_COLLECTION
import com.nullpointer.nullsiteadmin.models.dto.CredentialsDTO
import com.nullpointer.nullsiteadmin.models.dto.UpdateInfoPhoneDTO
import com.nullpointer.nullsiteadmin.models.response.AuthResponse
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
            true-> refCollectionPhones.document(uuidPhone).update(updateInfoPhoneDTO.toUpdateMap())
            false -> refCollectionPhones.document(uuidPhone).set(updateInfoPhoneDTO.toCreateMap())
        }
    }


    suspend fun login(credentialsDTO: CredentialsDTO):AuthResponse{
        val response= auth.signInWithEmailAndPassword(
            credentialsDTO.email,
            credentialsDTO.password
        ).await()

        return AuthResponse(
            idUser = response.user!!.uid,
            email = response.user!!.email!!
        )
    }



     fun logout() {
        auth.signOut()
    }

}