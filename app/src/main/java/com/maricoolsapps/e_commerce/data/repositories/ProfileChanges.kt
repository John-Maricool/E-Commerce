package com.maricoolsapps.e_commerce.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.maricoolsapps.e_commerce.data.model.User
import com.maricoolsapps.e_commerce.utils.Resource
import javax.inject.Inject

import com.google.firebase.auth.EmailAuthProvider
import com.maricoolsapps.e_commerce.data.db.FirebaseAuthSource
import com.maricoolsapps.e_commerce.utils.Constants
import kotlinx.coroutines.tasks.await
import java.lang.Exception

/*
This class is where all the functions involving registration, profile changes etc are done
 */

class ProfileChanges
@Inject constructor(val source: FirebaseAuthSource) {

    suspend fun signInUser(user: User, b: (Resource<String>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.signinUser(user)
            b.invoke(Resource.success(Constants.successful))
        } catch (e: java.lang.Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

    suspend fun changeEmail(email: String, user: User, b: (Resource<String>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.changeEmail(email, user)
            b.invoke(Resource.success(Constants.successful))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

    fun getAuthState(): LiveData<Resource<FirebaseUser>> {
        val result = MutableLiveData<Resource<FirebaseUser>>()
        source.auth.addAuthStateListener {
            if (it.currentUser == null && it.uid == null) {
                result.postValue(Resource.error("No user currently", null))
            } else {
                result.postValue(Resource.success(it.currentUser))
            }
        }
        return result
    }

    suspend fun createNewUser(user: User, b: (Resource<String>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.createUser(user)
            b.invoke(Resource.success(Constants.successful))
        } catch (e: java.lang.Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

    suspend fun resetPassword(email: String, b: (Resource<String>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.resetPassword(email)
            b.invoke(Resource.success("Email has been sent to $email"))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), "Email dosen't exist"))
        }
    }
}