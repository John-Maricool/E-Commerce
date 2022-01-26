package com.maricoolsapps.e_commerce.firebase

import android.R.attr
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.maricoolsapps.e_commerce.model.User
import com.maricoolsapps.e_commerce.utils.Resource
import javax.inject.Inject
import android.R.attr.password

import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.tasks.await

/*
This class is where all the functions involving registration, profile changes etc are done
 */

class ProfileChanges
@Inject constructor(val auth: FirebaseAuth) {

    fun signInUser(user: User): LiveData<Resource<String>> {
        val userValue = MutableLiveData<Resource<String>>()
        //this is where the user signs up for the first time
        auth.signInWithEmailAndPassword(user.email, user.password).addOnSuccessListener {
            userValue.value = Resource.success("Successful Login")
        }.addOnFailureListener {
            userValue.value = Resource.error("Error", it.message!!)
        }
        return userValue
    }

    suspend fun changeEmail(user: FirebaseUser, email: String, password: String, newEmail: String):
            Resource<String> {
        return try {
            val credential =
                EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential).await()
            user.updateEmail(newEmail).await()
            Resource.success("Success")

        } catch (E: Exception) {
            Resource.error("Error", null)
        }
    }

    fun getAuthState(): LiveData<Resource<FirebaseUser>> {
        val result = MutableLiveData<Resource<FirebaseUser>>()
        auth.addAuthStateListener {
            if (it.currentUser == null && it.uid == null) {
                result.postValue(Resource.error("No user currently", null))
            } else {
                result.postValue(Resource.success(it.currentUser))
            }
        }
        return result
    }

    fun createNewUser(user: User): LiveData<Resource<String>> {
        val userValue = MutableLiveData<Resource<String>>()
        //this is where the user signs up for the first time
        auth.createUserWithEmailAndPassword(user.email, user.password).addOnSuccessListener {
            userValue.value = Resource.success("Successful Login")
        }.addOnFailureListener {
            userValue.value = Resource.error("Error", it.message!!)
        }
        return userValue
    }

    fun resetPassword(email: String): LiveData<Resource<String>> {
        val valueReset = MutableLiveData<Resource<String>>()
        //this is when the user clicks on forgot password.
        //an email is sent to him
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener {
            valueReset.value = Resource.success("Email has been sent to $email")
        }.addOnFailureListener {
            valueReset.value = Resource.error("Error, email doesn't exist", it.message!!)
        }
        return valueReset
    }
}