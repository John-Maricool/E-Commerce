package com.maricoolsapps.e_commerce.data.db

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.maricoolsapps.e_commerce.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthSource
@Inject constructor(var auth: FirebaseAuth){

    suspend fun signinUser(user: User): AuthResult? {
        return auth.signInWithEmailAndPassword(user.email, user.password).await()
    }

    suspend fun createUser(user: User): AuthResult? {
        return auth.createUserWithEmailAndPassword(user.email, user.password).await()
    }

    suspend fun resetPassword(email: String): Void? {
        return auth.sendPasswordResetEmail(email).await()
    }

    suspend fun changeEmail(email: String, user: User): FirebaseUser? {
        val provider = EmailAuthProvider.getCredential(user.email, user.password)
        return auth.currentUser?.apply {
            reauthenticate(provider).await()
            updateEmail(email).await()
        }
    }
}