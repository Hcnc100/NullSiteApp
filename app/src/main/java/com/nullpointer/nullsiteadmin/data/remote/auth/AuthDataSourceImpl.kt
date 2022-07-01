package com.nullpointer.nullsiteadmin.data.remote.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class AuthDataSourceImpl : AuthDataSource {

    private val auth = Firebase.auth

    override suspend fun authWithEmailAndPassword(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).await()
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