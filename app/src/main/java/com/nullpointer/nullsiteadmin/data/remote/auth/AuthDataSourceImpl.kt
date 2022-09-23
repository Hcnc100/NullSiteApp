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
        private const val FIELD_CREATE_USER = "createdAt"
        private const val FIELD_ARRAY_TOKENS = "tokens"
    }

    private val auth = Firebase.auth
    private val messaging = Firebase.messaging
    private val refCollectionTokens = Firebase.firestore.collection(COLLECTION_TOKEN)
    private val database = Firebase.firestore


    override suspend fun addingTokenUser(newToken: String?, uuidUser: String?, oldToken: String) {

        val idDocument = uuidUser ?: auth.currentUser!!.uid
        val finishToken = newToken ?: Firebase.messaging.token.await()

        val refToken = refCollectionTokens.document(idDocument)

        database.runTransaction { transaction ->
            val currentToken = transaction.get(refToken)
            // && (currentToken.get(FIELD_ARRAY_TOKENS) as? List<*>) == null
            if (currentToken.exists()) {

                (currentToken.get(FIELD_ARRAY_TOKENS) as? HashMap<*, *>)?.let {
                    if (!it.containsKey(finishToken)) {
                        transaction.update(
                            /* documentRef = */
                            refToken,
                            /* field = */
                            "$FIELD_ARRAY_TOKENS.$finishToken",
                            /* value = */
                            mapOf(FIELD_CREATE_USER to FieldValue.serverTimestamp()),
                        )
                    }

                    if (it.containsKey(oldToken)) {
                        transaction.update(
                            /* documentRef = */
                            refToken,
                            /* field = */
                            "$FIELD_ARRAY_TOKENS.$oldToken",
                            /* value = */
                            FieldValue.delete(),
                        )
                    }
                }

            } else {
                transaction.set(
                    /* documentRef = */
                    refToken,
                    /* data = */
                    hashMapOf(
                        FIELD_ARRAY_TOKENS to mapOf(
                            finishToken to mapOf(FIELD_CREATE_USER to FieldValue.serverTimestamp())
                        )
                    ),
                )
            }
        }.await()
    }

    override suspend fun authWithEmailAndPassword(email: String, pass: String): UserAuth {
        val response = auth.signInWithEmailAndPassword(email, pass).await()
        val user = response.user!!
        val token = Firebase.messaging.token.await()
        addingTokenUser(uuidUser = user.uid, newToken = token)
        return UserAuth(
            id = user.uid,
            email = user.email ?: "",
            tokenMsg = token
        )
    }


    override suspend fun logout() = auth.signOut()
    override suspend fun getUserToken(): String = messaging.token.await()
}