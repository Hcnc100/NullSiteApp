package com.nullpointer.nullsiteadmin.data.remote.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class AuthDataSourceImpl : AuthDataSource {

    companion object {
        private const val COLLECTION_TOKEN = "tokens"
    }

    private val auth = Firebase.auth
    private val refCollectionTokens = Firebase.firestore.collection(COLLECTION_TOKEN)

    override suspend fun updateTokenUser(
        token: String?,
        uuidUser: String?
    ) {
        val idDocument = uuidUser ?: auth.currentUser?.uid!!
        val refNodeUser = refCollectionTokens.document(idDocument)
        val docUser = refNodeUser.get().await()
        val finishToken = token ?: Firebase.messaging.token.await()
        if (docUser.exists()) {
            refNodeUser.update(
                mapOf(
                    "lastUpdate" to FieldValue.serverTimestamp(),
                    "token" to finishToken
                )
            )
        } else {
            refNodeUser.set(
                mapOf(
                    "lastUpdate" to FieldValue.serverTimestamp(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "token" to finishToken
                )
            )
        }
    }

    override suspend fun authWithEmailAndPassword(email: String, pass: String) {
        val response = auth.signInWithEmailAndPassword(email, pass).await()
        updateTokenUser(uuidUser = response.user?.uid)
    }

    override suspend fun logout() =
        auth.signOut()


    override fun isAuthUser() = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            trySend(it.currentUser != null)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            Timber.d("Remove listener auth")
            auth.removeAuthStateListener(listener)
        }
    }
}