package com.nullpointer.nullsiteadmin.data.remote.auth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.nullpointer.nullsiteadmin.models.UserAuth
import kotlinx.coroutines.tasks.await

class AuthDataSourceImpl : AuthDataSource {

    companion object {
        private const val COLLECTION_TOKEN = "tokens"
        private const val FIELD_TOKEN_USER = "token"
        private const val FIELD_CREATE_USER = "createdAt"
        private const val FIELD_UPDATE_USER = "lastUpdate"
    }

    private val auth = Firebase.auth
    private val messaging = Firebase.messaging
    private val refCollectionTokens = Firebase.firestore.collection(COLLECTION_TOKEN)

    override suspend fun updateTokenUser(
        token: String?,
        uuidUser: String?
    ) {
        val idDocument = uuidUser ?: auth.currentUser!!.uid
        val refNodeUser = refCollectionTokens.document(idDocument)
        val docUser = refNodeUser.get().await()
        val finishToken = token ?: Firebase.messaging.token.await()
        val operation = if (docUser.exists()) {
            refNodeUser.update(
                mapOf(
                    FIELD_UPDATE_USER to FieldValue.serverTimestamp(),
                    FIELD_TOKEN_USER to finishToken
                )
            )
        } else {
            refNodeUser.set(
                mapOf(
                    FIELD_UPDATE_USER to FieldValue.serverTimestamp(),
                    FIELD_CREATE_USER to FieldValue.serverTimestamp(),
                    FIELD_TOKEN_USER to finishToken
                )
            )
        }

        operation.await()
    }

    override suspend fun authWithEmailAndPassword(email: String, pass: String): UserAuth {
        val response = auth.signInWithEmailAndPassword(email, pass).await()
        val user = response.user!!
        val token = Firebase.messaging.token.await()
        updateTokenUser(uuidUser = user.uid, token = token)
        return UserAuth(
            id = user.uid,
            email = user.email ?: "",
            tokenMsg = token
        )
    }


    override suspend fun logout() = auth.signOut()
    override suspend fun getUserToken(): String = messaging.token.await()
}