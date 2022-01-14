package com.maricoolsapps.e_commerce.firebase

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.maricoolsapps.e_commerce.model.User
import com.maricoolsapps.e_commerce.utils.Resource
import javax.inject.Inject

/*
This class is where all the functions involving registration, profile changes etc are done
 */

class ProfileChanges
@Inject constructor(private val auth: FirebaseAuth){

    fun signInUser(user: User): LiveData<Resource<String>>{
        val userValue = MutableLiveData<Resource<String>>()
        //this is where the user signs up for the first time
        auth.signInWithEmailAndPassword(user.email, user.password).addOnSuccessListener{
           userValue.value = Resource.success("Successful Login")
        }.addOnFailureListener{
            userValue.value = Resource.error("Error",it.message!!)
        }
        return userValue
    }

   fun createNewUser(user: User): LiveData<Resource<String>>{
        val userValue = MutableLiveData<Resource<String>>()
        //this is where the user signs up for the first time
        auth.createUserWithEmailAndPassword(user.email, user.password).addOnSuccessListener{
            userValue.value = Resource.success("Successful Login")
        }.addOnFailureListener{
            userValue.value = Resource.error("Error",it.message!!)
        }
        return userValue
    }

    fun resetPassword(email: String): LiveData<Resource<String>>{
        val valueReset = MutableLiveData<Resource<String>>()
        //this is when the user clicks on forgot password.
        //an email is sent to him
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener {
            valueReset.value = Resource.success("Email has been sent to $email")
        }.addOnFailureListener{
            valueReset.value = Resource.error("Error, email dosen't exist", it.message!!)
        }
        return valueReset
    }

    fun getProfilePhoto(): LiveData<Resource<Uri>>{
        val photoValue = MutableLiveData<Resource<Uri>>()
        try{
            photoValue.value = Resource.success(auth.currentUser?.photoUrl)
        }catch (e: Exception){
            photoValue.value = Resource.error("Error retrieving photo", e.message?.toUri())
        }
        return photoValue
    }

    fun changeProfilePhotoAndName(uri: Uri?, name: String): LiveData<Resource<String>> {
        val profileChanges = MutableLiveData<Resource<String>>()
        try {
            val profile = UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .setDisplayName(name)
                .build()

            auth.currentUser?.updateProfile(profile)?.addOnSuccessListener {
                profileChanges.value = Resource.success("Successfully changed")
            }?.addOnFailureListener {
                profileChanges.value = Resource.error("Error Changing photo", it.message)
            }
        } catch (e: Exception) {
            profileChanges.value = Resource.error("Check your internet connection", e.message)
        }
        return profileChanges
    }
}